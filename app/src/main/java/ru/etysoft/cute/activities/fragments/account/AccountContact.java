package ru.etysoft.cute.activities.fragments.account;

public interface AccountContact {
    interface Model{

    }
    interface View{
        void initializeViews();

        void setAccountInfo(String login, String status, String photo, int id);
    }
    interface Presenter{
        void updateData();
    }
}
