package ru.etysoft.cute.fragments.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import ru.etysoft.cute.R;
import ru.etysoft.cute.utils.CustomToast;


public class DialogsFragment extends Fragment {

    private DialogsViewModel dialogsViewModel;

    public static DialogsFragment newInstance() {
        return new DialogsFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        dialogsViewModel =
                ViewModelProviders.of(this).get(DialogsViewModel.class);
        View root = inflater.inflate(R.layout.activity_meet, container, false);
        Button button = root.findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomToast.show("hey", R.drawable.icon_success, getActivity());
            }
        });
        return root;
    }


}