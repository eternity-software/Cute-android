package ru.etysoft.cute.activities.fragments.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.music.Music;
import ru.etysoft.cute.activities.SettingsActivity;
import ru.etysoft.cute.activities.EditProfile.EditProfileActivity;
import ru.etysoft.cute.activities.friends.FriendsActivity;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.themes.Theme;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cuteframework.methods.user.User;

public class AccountFragment extends Fragment implements AccountContact.View {
    private View view;

    private ImageView settingImageView;
    private ImageView editButton;
    private Avatar userimage;
    private TextView status;
    private TextView login;
    private LinearLayout friendsView;
    private LinearLayout musicView;
    private RecyclerView friendsRecyclerView;
    private TextView friendsCountView;
    private FriendsSnippetAdapter friendsSnippetAdapter;

    private List<User> friends;

    private AccountContact.Presenter presenter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.activity_account, container, false);
        view = root;
        Theme.applyBackground(view);
        friends = new ArrayList<User>();
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
        friendsView = view.findViewById(R.id.friendsView);
        musicView = view.findViewById(R.id.musicView);
        friendsCountView = view.findViewById(R.id.friendsCountView);
        friendsRecyclerView = view.findViewById(R.id.friendSnippets);
        // создаем адаптер
        friendsSnippetAdapter = new FriendsSnippetAdapter(getActivity(), friends);
        // устанавливаем для списка адаптер
        friendsRecyclerView.setAdapter(friendsSnippetAdapter);

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

        friendsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                getActivity().startActivity(intent);
            }
        });

        musicView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Music.class);
                getActivity().startActivity(intent);
            }
        });

        friendsRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FriendsActivity.class);
                getActivity().startActivity(intent);
            }
        });


    }


    @Override
    public void setAccountInfo(String login, String status, String photo, String id) {
        this.status.setText(status);
        this.login.setText(login);
        userimage.generateIdPicture(3);
        userimage.setAcronym(login, Avatar.Size.LARGE);
        if (photo != null) {
            try {

                Picasso.get().load(photo).placeholder(getResources().getDrawable(R.drawable.circle_gray)).transform(new CircleTransform()).into(userimage.getPictureView());
            }
            catch (Exception ignored)
            {
                // Not attached to context
            }
        }
    }

    @Override
    public List<User> getFriends() {
        return friends;
    }

    @Override
    public void updateFriendsViews() {
        friendsCountView.setText(String.valueOf(friends.size()));
        friendsSnippetAdapter.notifyDataSetChanged();
    }

    public AccountContact.Presenter getPresenter() {
        if (presenter == null) {
            presenter = new AccountPresenter(getActivity(), this);
        }
        return presenter;

    }


}