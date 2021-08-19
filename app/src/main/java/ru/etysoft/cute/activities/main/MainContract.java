package ru.etysoft.cute.activities.main;

import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.GetAccount.GetAccountResponse;


public interface MainContract {

    interface View {
        void setupNavigation();
    }

    interface Presenter {

        void startCrashHandler();

        void updateAccountData();

        void onDestroy();
    }

    interface Model {
        GetAccountResponse getAccountInfo(String token) throws ResponseException;
    }

}
