package ru.etysoft.cute.fragments.account;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.json.JSONException;
import org.json.JSONObject;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.SettingsActivity;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.utils.CustomToast;

public class AccountFragment extends Fragment {

    private AccountViewModel accountViewModel;

    private View view;

    public static AccountFragment newInstance() {
        return new AccountFragment();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        accountViewModel =
                ViewModelProviders.of(this).get(AccountViewModel.class);

        final View root = inflater.inflate(R.layout.activity_account, container, false);
        ImageView userIcon = root.findViewById(R.id.userimage);
        userIcon.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_uber));

        final AppSettings appSettings = new AppSettings(getActivity());
        TextView id = root.findViewById(R.id.idview);
        TextView username = root.findViewById(R.id.username);
        if (appSettings.getString("username") != null) {
            id.setText("u" + appSettings.getString("id"));
            username.setText(appSettings.getString("username"));
        }


        view = root;

        ImageView menuButton = view.findViewById(R.id.menuButton);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        return root;
    }


    public void updateData() {
        if (Methods.hasInternet(getContext())) {
            final AppSettings appSettings = new AppSettings(getActivity());
            APIRunnable apiRunnable = new APIRunnable() {
                @Override
                public void run() {
                    if (isSuccess()) {
                        try {
                            JSONObject jsonObject = new JSONObject(getResponse());
                            JSONObject data = jsonObject.getJSONObject("data");

                            TextView id = view.findViewById(R.id.idview);
                            id.setText("u" + data.getString("id"));

                            TextView username = view.findViewById(R.id.username);
                            username.setText(data.getString("login"));

                            appSettings.setString("username", data.getString("login"));
                            appSettings.setString("id", data.getString("id"));


                        } catch (JSONException e) {
                            CustomToast.show(getString(R.string.err_json), R.drawable.icon_error, getActivity());
                        }
                    }
                }
            };

            Methods.getMyAccount(appSettings.getString("session"), apiRunnable, getActivity());
        }
    }
}