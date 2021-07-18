package ru.etysoft.cute.activities.Meet;

import android.app.Activity;

public interface MeetContract {

    interface View {
        void setEasterText();
    }

    interface Presenter {
        void onSignInButtonClick(Activity contextActivity);

        void onSignUpButtonClick(Activity contextActivity);
    }

    interface Model {
        void openSignInActivity(Activity contextActivity);

        void openSignUpActivity(Activity contextActivity);
    }
}
