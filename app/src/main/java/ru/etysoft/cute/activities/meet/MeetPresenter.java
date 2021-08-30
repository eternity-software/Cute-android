package ru.etysoft.cute.activities.meet;

import android.app.Activity;
import android.content.Intent;

import ru.etysoft.cute.activities.signin.SignInActivity;
import ru.etysoft.cute.activities.signup.SignUpActivity;

public class MeetPresenter implements MeetContract.Presenter {

    private final MeetContract.View meetView;

    public MeetPresenter(MeetContract.View meetView) {
        this.meetView = meetView;
        meetView.setEasterText();
    }


    @Override
    public void onSignInButtonClick(Activity contextActivity) {
        Intent intent = new Intent(contextActivity, SignInActivity.class);
        contextActivity.startActivity(intent);
        contextActivity.finish();
    }

    @Override
    public void onSignUpButtonClick(Activity contextActivity) {
        Intent intent = new Intent(contextActivity, SignUpActivity.class);
        contextActivity.startActivity(intent);
        contextActivity.finish();
    }
}
