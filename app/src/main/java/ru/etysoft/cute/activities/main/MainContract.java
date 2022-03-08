package ru.etysoft.cute.activities.main;

import ru.etysoft.cuteframework.exceptions.ResponseException;



public interface MainContract {

    interface View {
        void setupNavigation();

        void startMeetActivity();

        void startConfirmationActivity();

        void showBannedBottomSheet();
    }

    interface Presenter {

        void startCrashHandler();

        void updateAccountData();

        void onDestroy();
    }

    interface Model {
        //GetAccountResponse getAccountInfo(String token) throws ResponseException;
    }

}
