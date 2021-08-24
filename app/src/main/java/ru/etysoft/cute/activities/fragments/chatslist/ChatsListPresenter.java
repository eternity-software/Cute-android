package ru.etysoft.cute.activities.fragments.chatslist;

import android.app.Activity;

import java.util.List;

import ru.etysoft.cute.activities.chatslist.ChatSnippetInfo;
import ru.etysoft.cute.activities.chatslist.ChatsListAdapter;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cuteframework.Methods;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.chat.Chat;
import ru.etysoft.cuteframework.methods.chat.ChatList.ChatListResponse;
import ru.etysoft.cuteframework.methods.chat.ChatSnippet;

public class ChatsListPresenter implements ChatsListContact.Presenter {

    private Activity context;
    private ChatsListContact.View view;

    private boolean updateListLock = false;

    public ChatsListPresenter(Activity activity, ChatsListContact.View view) {
        this.context = activity;
        this.view = view;
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
                            final boolean isDialog = !chat.getType().equals(Chat.Types.CONVERSATION);
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    chatsListAdapter.add(new ChatSnippetInfo(chat.getName(),
                                            chat.getLastMessageText(),
                                            chat.getLastMessageSenderDisplayName(),
                                            chat.getName().substring(0, 1),
                                            String.valueOf(chat.getId()),
                                            Numbers.getTimeFromTimestamp(chat.getLastMessageTime(), context),
                                            chat.isRead(), 0, true, isDialog, "null"));
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
                    } catch (ResponseException e) {
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
        }
    }

    @Override
    public void onDestroy() {

    }
}
