package ru.etysoft.cute.activities.fragments.dialogs;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DialogsViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DialogsViewModel() {

    }

    public LiveData<String> getText() {
        return mText;
    }
}