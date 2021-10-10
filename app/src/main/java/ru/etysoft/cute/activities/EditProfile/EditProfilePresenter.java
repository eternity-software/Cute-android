package ru.etysoft.cute.activities.EditProfile;

import android.app.Activity;


public class EditProfilePresenter implements EditProfileContract.Presenter {
    private final Activity context;
    private final EditProfileContract.View view;

    public EditProfilePresenter(Activity context, EditProfileContract.View view) {
        this.context = context;
        this.view = view;
    }

}
