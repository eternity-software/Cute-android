package ru.etysoft.cute.activities.fragments.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.EditProfile;
import ru.etysoft.cute.activities.SettingsActivity;

public class AccountFragment extends Fragment implements AccountContact.View{
    private View view;

    private ImageView settingImageView;
    private ImageView editButton;
    private ImageView userimage;
    private TextView status;
    private TextView login;

    private AccountContact.Presenter presenter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.activity_account, container, false);
        view = root;
        presenter = new AccountPresenter(getActivity(), this);
        initializeViews();
        presenter.updateData();
        return root;
    }





    @Override
    public void initializeViews() {
        settingImageView = view.findViewById(R.id.setting);
        settingImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivity(intent);
            }
        });
        editButton = view.findViewById(R.id.editProfile);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfile.class);
                intent.putExtra("name", "name");
                intent.putExtra("profilePhoto", "status");
                intent.putExtra("status", "status");
                startActivity(intent);
            }
        });
        userimage = view.findViewById(R.id.userimage);
        status = view.findViewById(R.id.status);
        login = view.findViewById(R.id.login);

    }

    @Override
    public void setPersonParam(String login, String status, String photo) {
        this.status.setText(status);
        this.login.setText(login);
    }
}