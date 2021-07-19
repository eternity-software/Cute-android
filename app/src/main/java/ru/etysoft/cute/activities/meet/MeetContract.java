package ru.etysoft.cute.activities.meet;

import android.app.Activity;

public interface MeetContract {

    interface View {
        void setEasterText();

        void initializeComponents();
    }

    interface Presenter {
        void onSignInButtonClick(Activity contextActivity);

        void onSignUpButtonClick(Activity contextActivity);
    }

    interface Model {
    }
}
