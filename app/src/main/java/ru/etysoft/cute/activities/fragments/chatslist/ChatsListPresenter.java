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
    public void updateChatsList(final ProgressBar progressBar, final ChatsListAdapter chatsListAdapter, final Toolbar toolbar, final ErrorPanel errorPanel,
                                final LinearLayout noChats, final LinearLayout errorContainer) {
        if (!updateListLock) {
            updateListLock = true;
            progressBar.setVisibility(View.VISIBLE);

            view.setStatusMessage(StringsRepository.getOrDefault(R.string.updating, context));


            progressBar.setVisibility(View.VISIBLE);

            noChats.setVisibility(View.INVISIBLE);
            chatsListAdapter.clear();

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
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                chatsListAdapter.notifyDataSetChanged();
                                if (chatsListAdapter.getCount() > 10) {
                                    AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL |
                                            AppBarLayout.LayoutParams.SCROLL_FLAG_SNAP |
                                            AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS);
                                    toolbar.setLayoutParams(params);
                                } else {
                                    AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                                    params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
                                    toolbar.setLayoutParams(params);
                                }
                                if (!finalHasMessages) {
                                    noChats.setVisibility(View.VISIBLE);
                                }

                            }
                        });

                    } catch (NotCachedException e) {
                        e.printStackTrace();
                    } catch (ResponseException e) {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppBarLayout.LayoutParams params = (AppBarLayout.LayoutParams) toolbar.getLayoutParams();
                                params.setScrollFlags(AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL);
                                toolbar.setLayoutParams(params);
                                errorPanel.show();
                                errorContainer.setVisibility(View.VISIBLE);
                            }
                        });

                        e.printStackTrace();
                    }
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.setStatusMessage(StringsRepository.getOrDefault(R.string.chats, context));

                            progressBar.setVisibility(View.INVISIBLE);
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
