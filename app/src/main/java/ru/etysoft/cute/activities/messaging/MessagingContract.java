package ru.etysoft.cute.activities.messaging;

import android.content.res.Resources;

import java.util.HashMap;
import java.util.Map;

import ru.etysoft.cute.activities.main.MainContract;
import ru.etysoft.cuteframework.methods.messages.Message;

public interface MessagingContract {

    interface View
    {
        void initViews();

        String getStatusText();

        String getChatName();

        Resources getResources();

        String getAccountId();

        String getStringsRepositoryResult(int resId);

        void setMessageRead(long messageId);

        void onSendMessageButtonClick(android.view.View view);

        void setupRecyclerView();

        void showErrorToast(String message);

        void addMessage(Message message);

        Map<String, Message> getMessagesIds();

        /*
            Isolation from activity context
         */
        void runOnUiThread(Runnable runnable);

        void showEmptyChatPanel(android.view.View view);

        void setHeaderInfo(String avatarUrl, String title, String status);

        void setStatus(String status);

        void setChatName(String chatName);

        void setAvatarImage(String avatarUrl);

    }

    interface Presenter {

        void registerChatSocket();

        String getChatType();

        String getAvatarUrl();

        void loadChatInfo();

        boolean isDialog();

        void onDestroy();

    }
}
