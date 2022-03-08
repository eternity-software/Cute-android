package ru.etysoft.cute.activities.confirmation;

import ru.etysoft.cuteframework.exceptions.ResponseException;



public interface ConfirmationContract {

    interface View {
        void showError(String text);

        void hideError();

        void showMainActivity();

        void setEmail(String email);

        void initializeViews();

        void setEnabledActionButton(boolean isEnabled);
    }

    interface Presenter {
        void onConfirmButtonClick(final String code);

        void onSendNewConfirmCodeButtonClick();

        void onCantReceiveButtonClick();

        void initializeNetworkStateHolder();

        void onDestroy();
    }

    interface Model {

    }
}
