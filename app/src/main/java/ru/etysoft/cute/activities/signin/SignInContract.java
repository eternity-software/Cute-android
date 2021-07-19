package ru.etysoft.cute.activities.signin;

import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.Authorization.AuthorizationResponse;

public interface SignInContract {

    interface View {
        void showError(String text);

        void hideError();

        void showMainActivity();

        void initializeViews();
    }

    interface Presenter {
        void onSignInButtonClick(final String login, final String password);

        void initializeNetworkStateHolder();

        void onDestroy();
    }

    interface Model {
        AuthorizationResponse sign(String login, String password) throws ResponseException;
    }


}

