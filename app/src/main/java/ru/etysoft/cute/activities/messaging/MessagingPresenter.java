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
import ru.etysoft.cuteframework.models.messages.SuperMessage;
import ru.etysoft.cuteframework.storage.Cache;


public class MessagingPresenter implements MessagingContract.Presenter {

    private MessagingContract.View view;
    private String chatType, avatarPath, chatId;
    private long membersCount;


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
                                view.showLoading();
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

        view.showLoading();
        view.hideErrorPanel();
        Thread loadingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GetChatHistoryRequest.GetChatHistoryResponse getChatHistoryResponse = new GetChatHistoryRequest(chatId).execute();
                    List<MessageComponent> messageComponentList = new ArrayList<>();
                    for(Message message : getChatHistoryResponse.getMessages())
                    {
                        if(message instanceof SuperMessage)
                        {

                            if(((SuperMessage) message).getSender().getId().equals(Cache.getUserAccount().getId()))
                            {
                                MessageComponent messageComponent = new MessageComponent(MessageComponent.TYPE_MY_MESSAGE);
                                messageComponent.setMessage(message);
                                messageComponentList.add(messageComponent);

                            }
                        }

                    }

                    view.addMessages(messageComponentList);
                    view.hideLoading();

                } catch (Exception e) {
                    view.hideLoading();
                    view.showErrorPanel();
                    e.printStackTrace();
                }
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
