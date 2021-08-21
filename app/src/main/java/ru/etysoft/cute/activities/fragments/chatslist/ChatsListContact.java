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
    }

    interface Presenter {

        void updateChatsList(final ProgressBar progressBar, final ChatsListAdapter chatsListAdapter, final Toolbar toolbar, final ErrorPanel errorPanel,
                             final android.widget.LinearLayout noChats, final LinearLayout errorContainer);

        void onDestroy();
    }
}
