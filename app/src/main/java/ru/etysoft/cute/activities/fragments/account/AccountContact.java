package ru.etysoft.cute.activities.fragments.account;

public interface AccountContact {
    interface Model{

    }
    interface View{
        void initializeViews();

        void setPersonParam(String login, String status, String photo);
    }
    interface Presenter{
        void updateData();
    }
}
