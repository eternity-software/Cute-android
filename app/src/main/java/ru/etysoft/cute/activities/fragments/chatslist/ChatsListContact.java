package ru.etysoft.cute.activities.fragments.chatslist;

import ru.etysoft.cute.activities.chatslist.ChatsListAdapter;

public interface ChatsListContact {

    interface View {
        void initViews();

        void setStatusMessage(String statusMessage);

        void showUpdateViews();

        void hideToolbar();

        void showToolbar();

        void showEmptyListView();

        void showErrorView();

        void hideUpdateViews();
    }

    interface Presenter {

        void updateChatsList(final ChatsListAdapter chatsListAdapter);

        void onDestroy();
    }
}
