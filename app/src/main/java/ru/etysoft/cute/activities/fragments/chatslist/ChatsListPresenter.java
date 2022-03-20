package ru.etysoft.cute.activities.fragments.chatslist;

import android.app.Activity;

import java.util.List;

import ru.etysoft.cute.activities.chatslist.ChatsListAdapter;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cuteframework.CuteFramework;
import ru.etysoft.cuteframework.methods.chat.ChatGetListRequest;
import ru.etysoft.cuteframework.models.ChatSnippet;


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

            context.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chatsListAdapter.getList().clear();
                    chatsListAdapter.notifyDataSetChanged();
                }
            });
            view.showUpdateViews();
            // Задаём обработчик запроса к API
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ChatGetListRequest.ChatGetListResponse chatListResponse = new ChatGetListRequest().execute();
                        final List<ChatSnippet> chats = chatListResponse.getChatSnippets();

                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatsListAdapter.getList().addAll(chats);
                                chatsListAdapter.notifyDataSetChanged();
                                if (chatsListAdapter.getItemCount() > 10) {
                                    view.hideToolbar();
                                } else {
                                    view.showToolbar();
                                }
                                if (chats.size() == 0) {
                                    view.showEmptyListView();
                                }
                            }
                        });
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
