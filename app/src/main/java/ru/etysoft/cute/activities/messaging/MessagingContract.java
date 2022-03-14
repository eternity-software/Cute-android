package ru.etysoft.cute.activities.messaging;

import android.content.res.Resources;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.etysoft.cute.activities.main.MainContract;

import ru.etysoft.cuteframework.models.messages.Message;


public interface MessagingContract {

    interface View {
        void initViews();

        String getStatusText();

        String getChatName();

        Resources getResources();

        void insertMessages(List<MessageComponent> messageComponents, int index);

        String getAccountId();

        MessagesAdapter getMessagesAdapter();

        String getStringsRepositoryResult(int resId);

        void setMessageRead(long messageId);

        void onSendMessageButtonClick(android.view.View view);

        void setupRecyclerView();

        void showErrorToast(String message);

        void showErrorPanel();

        void showInfoPanel();

        void hideInfoPanel();

        void hideErrorPanel();

        Map<String, Message> getMessagesIds();

        /*
            Isolation from activity context
         */
        void runOnUiThread(Runnable runnable);

        void showChatInfo(android.view.View view);

        void setHeaderInfo(String avatarUrl, String title, String status);

        void setStatus(String status);

        void setChatName(String chatName);

        void setAvatarImage(String avatarUrl);

        void showLoading();

        void hideLoading();

    }

    interface Presenter {

        String getChatType();

        String getAvatarUrl();

        void loadChatInfo();

        void loadLatestMessages();

        void loadUpperMessages();

        boolean isDialog();

        void onDestroy();

    }
}
