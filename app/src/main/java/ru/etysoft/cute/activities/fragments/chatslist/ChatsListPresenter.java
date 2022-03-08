package ru.etysoft.cute.activities.fragments.chatslist;

import android.app.Activity;

import java.util.List;

import ru.etysoft.cute.activities.chatslist.ChatsListAdapter;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cuteframework.CuteFramework;
import ru.etysoft.cuteframework.methods.chat.ChatList.ChatListRequest;
import ru.etysoft.cuteframework.methods.chat.ChatList.ChatListResponse;
import ru.etysoft.cuteframework.methods.chat.ChatSnippet;


public class ChatsListPresenter implements ChatsListContact.Presenter {

    private final Activity context;
    private final ChatsListContact.View view;


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
                        ChatListResponse chatListResponse = new ChatListRequest(CachedValues.getSessionKey(context)).execute();
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
    }
}
