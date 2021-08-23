package ru.etysoft.cute.activities.fragments.chatslist;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;

import com.google.android.material.appbar.AppBarLayout;

import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.chatslist.ChatSnippetInfo;
import ru.etysoft.cute.activities.chatslist.ChatsListAdapter;
import ru.etysoft.cute.components.ErrorPanel;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.lang.StringsRepository;
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
            view.showProgressBarAndSetStatusMessage();
            // Задаём обработчик запроса к API
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        ChatListResponse chatListResponse = Methods.getChatList(CachedValues.getSessionKey(context));
                        final List<ChatSnippet> chats = chatListResponse.getChats();
                        System.out.println(chats.size());
                        boolean hasMessages = false;
                        for (int i = 0; i < chats.size(); i++) {
                            final ChatSnippet chat = chats.get(i);
                            hasMessages = true;
                            final boolean isDialog = !chat.getType().equals(Chat.Types.CONVERSATION);
                            context.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    System.out.println(chat.getName());
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
                    final boolean finalHasMessages = hasMessages;
                    chatsListAdapter.notifyDataSetChanged();
                    if (chatsListAdapter.getCount() > 10) {
                        view.hideToolbar();
                    } else {
                        view.safeToolbar();
                    }
                    if (!finalHasMessages) {
                        view.noChats();
                    }
                    } catch (NotCachedException e) {
                        e.printStackTrace();
                    } catch (ResponseException e) {
                        view.responseException();
                        e.printStackTrace();
                    }
                    view.removeProgressBarAndSetStatusMessage();
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
