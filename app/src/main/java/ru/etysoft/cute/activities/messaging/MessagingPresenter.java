package ru.etysoft.cute.activities.messaging;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cuteframework.exceptions.NoSuchValueException;
import ru.etysoft.cuteframework.exceptions.NotCachedException;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.chat.ChatGetInfoRequest;
import ru.etysoft.cuteframework.methods.chat.GetChatHistoryRequest;
import ru.etysoft.cuteframework.models.Chat;
import ru.etysoft.cuteframework.models.messages.Message;
import ru.etysoft.cuteframework.models.messages.ServiceMessage;
import ru.etysoft.cuteframework.models.messages.SuperMessage;
import ru.etysoft.cuteframework.storage.Cache;


public class MessagingPresenter implements MessagingContract.Presenter {

    private MessagingContract.View view;
    private String chatType, avatarPath, chatId;
    private long membersCount;
    private boolean isLoadingMessages = false;


    public MessagingPresenter(MessagingContract.View view,
                              String chatType, String avatarPath, String chatId) {
        this.view = view;
        this.chatType = chatType;
        this.chatId = chatId;

    }

    @Override
    public void loadChatInfo() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ChatGetInfoRequest.ChatGetInfoResponse chatInfoResponse = (new ChatGetInfoRequest(chatId)).execute();

                    view.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                membersCount = chatInfoResponse.getChat().getMembersCount();
                                view.setStatus(chatInfoResponse.getChat().getMembersCount() + " " + view.getResources().getString(R.string.members));
                            } catch (NoSuchValueException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                } catch (final Exception e) {
                    view.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.showErrorToast(e.getMessage());

                        }
                    });
                }
            }
        });
        thread.start();
    }

    @Override
    public void loadLatestMessages() {

        if(isLoadingMessages) return;
        isLoadingMessages = true;
        view.showLoading();
        view.hideErrorPanel();
        Thread loadingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GetChatHistoryRequest.GetChatHistoryResponse getChatHistoryResponse = new GetChatHistoryRequest(chatId).execute();
                    List<MessageComponent> messageComponentList = handleResponse(getChatHistoryResponse);

                    view.insertMessages(messageComponentList, view.getMessagesAdapter().getItemCount());
                    view.hideLoading();
                    view.notifyDataSetChanged();


                } catch (Exception e) {
                    view.hideLoading();
                    view.showErrorPanel();
                    e.printStackTrace();
                }
                isLoadingMessages = false;
            }
        });
        loadingThread.start();
    }


    private  List<MessageComponent> handleResponse(GetChatHistoryRequest.GetChatHistoryResponse getChatHistoryResponse ) throws NoSuchValueException {
        List<MessageComponent> messageComponentList = new ArrayList<>();
        for(Message message : getChatHistoryResponse.getMessages())
        {
            if(message instanceof SuperMessage)
            {


                MessageComponent messageComponent = new MessageComponent(MessageComponent.TYPE_MY_MESSAGE);
                messageComponent.setMessage(message);
                messageComponentList.add(messageComponent);


            }
            else if(message instanceof ServiceMessage)
            {
                MessageComponent messageComponent = new MessageComponent(MessageComponent.TYPE_SERVICE_MESSAGE);
                messageComponent.setMessage(message);
                messageComponentList.add(messageComponent);
            }

        }
        return messageComponentList;
    }

    @Override
    public void loadUpperMessages(Runnable onSuccess) {
        if(isLoadingMessages) return;
        isLoadingMessages = true;
        Thread loadingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GetChatHistoryRequest.GetChatHistoryResponse getChatHistoryResponse = new GetChatHistoryRequest(chatId, view.getMessagesAdapter().getMessageComponent(0).getMessage().getId()).execute();
                    List<MessageComponent> messageComponentList = handleResponse(getChatHistoryResponse);


                    view.insertMessages(messageComponentList, 0);
                    view.notifyDataSetChanged();
                    onSuccess.run();


                } catch (Exception e) {

                    e.printStackTrace();
                }
                isLoadingMessages = false;
            }
        });
        loadingThread.start();
    }


    @Override
    public String getChatType() {
        return chatType;
    }

    @Override
    public String getAvatarUrl() {
        return avatarPath;
    }

    @Override
    public boolean isDialog() {
        return chatType.equals(Chat.TYPE_PRIVATE);
    }

    @Override
    public void onDestroy() {

    }
}
