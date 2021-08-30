package ru.etysoft.cute.activities.fragments.account;

import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cuteframework.methods.friend.Friend;

public interface AccountContact {
    interface Model{

    }
    interface View{
        void initializeViews();

        void setAccountInfo(String login, String status, String photo, int id);

        List<Friend> getFriends();

        void updateFriendsViews();
    }
    interface Presenter{
        void openAvatar();

        void updateData();

        void updateFriends();
    }
}
