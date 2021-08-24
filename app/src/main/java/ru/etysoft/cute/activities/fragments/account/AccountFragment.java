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
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cuteframework.Methods;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.GetAccount.GetAccountResponse;

public class AccountFragment extends Fragment {


    private String name;
    private String urlPhoto = null;
    private String status = "";

    private View view;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        final View root = inflater.inflate(R.layout.activity_account, container, false);


        view = root;


        ImageView menuButton = view.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivity(intent);

            }
        });

        Button editButton = view.findViewById(R.id.editProfile);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfile.class);
                intent.putExtra("name", name);
                intent.putExtra("profilePhoto", status);
                intent.putExtra("status", status);
                startActivity(intent);
            }
        });

        return root;
    }


    public void updateData() {
        final TextView loginView = view.findViewById(R.id.login_view);
        final TextView displayNameView = view.findViewById(R.id.display_name_view);
        final TextView statusView = view.findViewById(R.id.status);

        try {
            displayNameView.setText(CachedValues.getDisplayName(getActivity()));
            loginView.setText(CachedValues.getLogin(getActivity()));
        } catch (NotCachedException e) {
            e.printStackTrace();
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final GetAccountResponse getAccountResponse = Methods.getInfo(CachedValues.getSessionKey(getActivity()));
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CachedValues.setDisplayName(getActivity(), getAccountResponse.getDisplayName());
                            CachedValues.setLogin(getActivity(), getAccountResponse.getLogin());
                            CachedValues.setId(getActivity(), getAccountResponse.getId());
                            try {
                                displayNameView.setText(CachedValues.getDisplayName(getActivity()));
                                loginView.setText(CachedValues.getLogin(getActivity()));
                                statusView.setText(CachedValues.getId(getActivity()));
                                Avatar avatar = (Avatar) view.findViewById(R.id.userimage);
                                if (avatar != null) {
                                    avatar.generateIdPicture(Integer.parseInt(getAccountResponse.getId()));
                                    avatar.setAcronym(getAccountResponse.getDisplayName());
                                }
                            } catch (NotCachedException ignored) {
                            }

                        }
                    });
                } catch (ResponseException e) {
                    e.printStackTrace();
                } catch (NotCachedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }
}