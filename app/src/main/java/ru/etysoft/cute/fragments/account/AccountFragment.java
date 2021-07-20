package ru.etysoft.cute.fragments.account;

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
import androidx.lifecycle.ViewModelProviders;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.EditProfile;
import ru.etysoft.cute.activities.SettingsActivity;
import ru.etysoft.cute.data.CacheUtils;

public class AccountFragment extends Fragment {

    private AccountViewModel accountViewModel;

    private String name;
    private String urlPhoto = null;
    private String status = "";

    private View view;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        accountViewModel =
                ViewModelProviders.of(this).get(AccountViewModel.class);

        final View root = inflater.inflate(R.layout.activity_account, container, false);

        final CacheUtils cacheUtils = CacheUtils.getInstance();
        TextView id = root.findViewById(R.id.idview);
        final TextView username = root.findViewById(R.id.username);


        view = root;

        updateData();
        ImageView menuButton = view.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                getActivity().startActivity(intent);
                // getActivity().overridePendingTransition(R.anim.slide_to_right, R.anim.slide_to_left);
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
        final CacheUtils cacheUtils = CacheUtils.getInstance();
        final ImageView profileImage = view.findViewById(R.id.userimage);

    }
}