package ru.etysoft.cute.activities.fragments.chatslist;

import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;

import ru.etysoft.cute.activities.chatslist.ChatsListAdapter;
import ru.etysoft.cute.components.ErrorPanel;

public interface ChatsListContact {

    interface View {
        void initViews();

        void setStatusMessage(String statusMessage);

        void showProgressBarAndSetStatusMessage();

        void hideToolbar();

        void safeToolbar();

        void noChats();

        void responseException();

        void removeProgressBarAndSetStatusMessage();
    }

    interface Presenter {

        void updateChatsList(final ChatsListAdapter chatsListAdapter);

        void onDestroy();
    }
}
