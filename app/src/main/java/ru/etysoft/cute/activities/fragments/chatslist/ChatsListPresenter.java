package ru.etysoft.cute.activities.fragments.chatslist;

import android.app.Activity;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.MessagingActivity;
import ru.etysoft.cute.activities.chatslist.ChatsListAdapter;
import ru.etysoft.cute.activities.messages.MessageInfo;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.lang.StringsRepository;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cuteframework.Methods;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.chat.Chat;
import ru.etysoft.cuteframework.methods.chat.ChatList.ChatListResponse;
import ru.etysoft.cuteframework.methods.chat.ChatSnippet;
import ru.etysoft.cuteframework.methods.chat.ServiceData;
import ru.etysoft.cuteframework.methods.messages.Message;
import ru.etysoft.cuteframework.sockets.methods.ChatList.ChatListSocket;
import ru.etysoft.cuteframework.sockets.methods.Messages.MessagesSocket;

public class ChatsListPresenter implements ChatsListContact.Presenter {

    private final Activity context;
    private final ChatsListContact.View view;
    private ChatListSocket chatListSocket;

    private boolean updateListLock = false;

    public ChatsListPresenter(Activity activity, ChatsListContact.View view) {
        this.context = activity;
        this.view = view;

    }

    public void createSocket()
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    chatListSocket = new ChatListSocket(CachedValues.getSessionKey(context), new ChatListSocket.ChatReceiveHandler() {
                        @Override
                        public void onMessageReceive(ChatSnippet chatSnippet) {
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    view.updateChatList();
                                }
                            });

                        }
                    });

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public void updateChatsList(final ChatsListAdapter chatsListAdapter) {
        if (!updateListLock) {
            updateListLock = true;
            chatsListAdapter.clear();
            view.showUpdateViews();
            // Задаём обработчик запроса к API
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ChatListResponse chatListResponse = Methods.getChatList(CachedValues.getSessionKey(context));
                        final List<ChatSnippet> chats = chatListResponse.getChats();

                        for (int i = 0; i < chats.size(); i++) {
                            final ChatSnippet chat = chats.get(i);
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatsListAdapter.add(chat);
                                }
                            });

                        }
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatsListAdapter.notifyDataSetChanged();
                                if (chatsListAdapter.getCount() > 10) {
                                    view.hideToolbar();
                                } else {
                                    view.showToolbar();
                                }
                                if (chats.size() == 0) {
                                    view.showEmptyListView();
                                }
                            }
                        });

                    } catch (NotCachedException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                view.showErrorView();
                            }
                        });
                        e.printStackTrace();
                    }
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.hideUpdateViews();
                        }
                    });
                    updateListLock = false;
                }
            });
            thread.start();
            createSocket();
        }
    }

    @Override
    public void onDestroy() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    chatListSocket.getWebSocket().getUserSession().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
}
