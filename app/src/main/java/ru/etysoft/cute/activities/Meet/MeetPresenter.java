package ru.etysoft.cute.activities.Meet;

import android.app.Activity;

public class MeetPresenter implements MeetContract.Presenter {

    private MeetModel meetModel;
    private MeetContract.View meetView;

    public MeetPresenter(MeetContract.View meetView) {
        this.meetModel = new MeetModel();
        this.meetView = meetView;
        meetView.setEasterText();
    }


    @Override
    public void onSignInButtonClick(Activity contextActivity) {
        meetModel.openSignInActivity(contextActivity);
    }

    @Override
    public void onSignUpButtonClick(Activity contextActivity) {
        meetModel.openSignUpActivity(contextActivity);
    }
}
