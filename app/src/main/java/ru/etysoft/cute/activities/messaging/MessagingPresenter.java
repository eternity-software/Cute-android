package ru.etysoft.cute.activities.messaging;

import android.widget.TextView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.lang.StringsRepository;

import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.chat.Chat;
import ru.etysoft.cuteframework.methods.chat.GetInfo.ChatInfoRequest;
import ru.etysoft.cuteframework.methods.chat.GetInfo.ChatInfoResponse;
import ru.etysoft.cuteframework.methods.messages.Message;
import ru.etysoft.cuteframework.sockets.Event;

public class MessagingPresenter implements MessagingContract.Presenter {

    private MessagingContract.View view;
    private String chatType, avatarPath, membersCount, session, chatId;

    public MessagingPresenter(MessagingContract.View view,
                              String chatType, String avatarPath, String session, String chatId) {
        this.view = view;
        this.chatType = chatType;
        this.session = session;
        this.chatId = chatId;
    }


    public void loadChatInfo() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ChatInfoResponse chatInfoResponse = (new ChatInfoRequest(session, chatId)).execute();

                    view.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                membersCount = String.valueOf(chatInfoResponse.getMembers().size());
                                view.setStatus(chatInfoResponse.getMembers().size() + " " + view.getResources().getString(R.string.members));
                            } catch (ResponseException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                } catch (ResponseException e) {
                    e.printStackTrace();
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
    public void registerChatSocket() {
        Thread backgroundWorker = new Thread(new Runnable() {
            @Override
            public void run() {
               // final ChatSocket.EventReceiveHandler eventReceiveHandler = new ChatSocket.EventReceiveHandler() {
                 //   @Override
                  //  public void onEventReceived(final Event event) {
//                        view.runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                try {
//
//                                    String statusText = "";
//                                    if (event instanceof NewMessageEvent) {
//                                        NewMessageEvent newMessageEvent = (NewMessageEvent) event;
//                                        if(newMessageEvent.getMessage().getSender().getId() == Long.parseLong(view.getAccountId()))
//                                        {
//                                            //view.setMessageRead(newMessageEvent.getMessage().getId());
//                                        }
//                                        if (!view.getMessagesIds().containsKey(String.valueOf(newMessageEvent.getMessage().getId()))) {
//                                            view.addMessage(newMessageEvent.getMessage());
//                                            view.getMessagesIds().put(String.valueOf(newMessageEvent.getMessage().getId()), newMessageEvent.getMessage());
//                                        }
//                                    } else if (event instanceof MemberStateChangedEvent) {
//                                        MemberStateChangedEvent memberStateChangedEvent = (MemberStateChangedEvent) event;
//                                        if (memberStateChangedEvent.getState().equals(MemberStateChangedEvent.States.TYPING)) {
//                                            if (isDialog()) {
//                                                statusText = view.getResources().getString(R.string.typing).replace("%s", view.getChatName());
//                                            } else {
//                                                statusText = view.getResources().getString(R.string.typing).replace("%s",
//                                                        String.valueOf(memberStateChangedEvent.getAccountId()));
//                                            }
//                                        } else {
//                                            if (isDialog()) {
//                                                if (memberStateChangedEvent.getState().equals(MemberStateChangedEvent.States.OFFLINE)) {
//                                                    statusText = view.getStringsRepositoryResult(R.string.offline);
//                                                } else if (memberStateChangedEvent.getState().equals(MemberStateChangedEvent.States.ONLINE)) {
//                                                    statusText = view.getStringsRepositoryResult(R.string.online);
//                                                }
//                                            } else {
//                                                statusText = membersCount + " " + view.getResources().getString(R.string.members);
//                                            }
//                                        }
//                                    }
//                                    else if(event instanceof MessageReadEvent)
//                                    {
//                                        MessageReadEvent messageReadEvent = (MessageReadEvent) event;
//                                        view.setMessageRead(messageReadEvent.getMessageId());
//                                    }
//                                    view.setStatus(statusText);
//
//
//                                } catch (Exception e) {
//                                    e.printStackTrace();
//                                }
//
//                            }
//                        });
                   // }
             //   };


            }
        });
        backgroundWorker.start();
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
        return chatType.equals(Chat.Types.PRIVATE);
    }

    @Override
    public void onDestroy() {

    }
}
