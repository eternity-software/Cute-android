package ru.etysoft.cute.activities.fragments.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.squareup.picasso.Picasso;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.SettingsActivity;
import ru.etysoft.cute.activities.editprofile.EditProfileActivity;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.utils.CircleTransform;

public class AccountFragment extends Fragment implements AccountContact.View{
    private View view;

    private ImageView settingImageView;
    private ImageView editButton;
    private Avatar userimage;
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
        userimage = view.findViewById(R.id.userimage);
        status = view.findViewById(R.id.statusView);
        login = view.findViewById(R.id.login);
        settingImageView = view.findViewById(R.id.setting);

        userimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPresenter().openAvatar();
            }
        });

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
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                intent.putExtra("name", "");
                intent.putExtra("profilePhoto", "status");
                intent.putExtra("status", status.getText());
                intent.putExtra("login", login.getText());
                startActivity(intent);
            }
        });


    }

    @Override
    public void setAccountInfo(String login, String status, String photo, int id) {
        this.status.setText(status);
        this.login.setText(login);
        userimage.generateIdPicture(id);
        userimage.setAcronym(login);
        if(photo != null) {
            Picasso.get().load(photo).placeholder(getResources().getDrawable(R.drawable.circle_gray)).transform(new CircleTransform()).into(userimage.getPictureView());
        }
    }

    public AccountContact.Presenter getPresenter() {
        if(presenter == null) {
            presenter = new AccountPresenter(getActivity(), this);
        }
        return presenter;

    }
}