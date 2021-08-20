package ru.etysoft.cute.activities.signup;

import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.Registration.RegistrationResponse;


public interface SignUpContract {

    interface View {
        void showError(String text);

        void hideError();

        void showConfirmationActivity();

        void initializeViews();

        boolean isPasswordsCorrect();

        void setEnabledActionButton(boolean isEnabled);
    }

    interface Presenter {
        void onSignUpButtonClick(final String login, final String displayName, final String email, final String password);

        void initializeNetworkStateHolder();

        void onDestroy();
    }

    interface Model {
        RegistrationResponse signUp(String login, String displayName, String email, String password) throws ResponseException;
    }
}
