package ru.etysoft.cute.activities.Meet;

import android.app.Activity;
import android.content.Intent;

import ru.etysoft.cute.activities.Signin;
import ru.etysoft.cute.activities.Signup;

public class MeetModel implements MeetContract.Model {
    @Override
    public void openSignInActivity(Activity contextActivity) {
        Intent intent = new Intent(contextActivity, Signin.class);
        contextActivity.startActivity(intent);
        contextActivity.finish();
    }

    @Override
    public void openSignUpActivity(Activity contextActivity) {
        Intent intent = new Intent(contextActivity, Signup.class);
        contextActivity.startActivity(intent);
        contextActivity.finish();
    }
}
