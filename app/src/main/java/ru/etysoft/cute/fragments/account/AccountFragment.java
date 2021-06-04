package ru.etysoft.cute.fragments.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.EditProfile;
import ru.etysoft.cute.activities.SettingsActivity;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.ImagesWorker;

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

        final AppSettings appSettings = new AppSettings(getActivity());
        TextView id = root.findViewById(R.id.idview);
        final TextView username = root.findViewById(R.id.username);
        if (appSettings.getString("username") != null) {
            name = appSettings.getString("username");
            id.setText("u" + appSettings.getString("id"));
            username.setText(name);
        }

        urlPhoto = appSettings.getString("profilePhoto");
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
        final AppSettings appSettings = new AppSettings(getActivity());
        final ImageView profileImage = view.findViewById(R.id.userimage);
        if (Methods.hasInternet(getContext())) {

            APIRunnable apiRunnable = new APIRunnable() {
                @Override
                public void run() {
                    if (isSuccess()) {
                        try {
                            JSONObject jsonObject = new JSONObject(getResponse());
                            final JSONObject data = jsonObject.getJSONObject("data").getJSONObject("account");
                            // String link = Methods.mainDomain + data.getString("photo") + "?size=250";
                            // appSettings.setString("profilePhoto", link);
                            final TextView id = view.findViewById(R.id.idview);
                            id.setText("u" + data.getString("id"));
                            boolean hasImage = false;

                            String name = data.getString("display_name") + " " + ((data.isNull("display_surname")) ? "" : data.get("display_surname"));
                            String statusText = data.getString("display_status_text");

                            TextView username = view.findViewById(R.id.username);
                            username.setText(name);

                            TextView status = view.findViewById(R.id.status);

                            if (data.isNull("display_status_text")) {
                                status.setText(getResources().getString(R.string.no_status));

                            } else {
                                AccountFragment.this.status = statusText;
                                status.setText(statusText);
                            }


                            // hasImage = (!link.equals("null"));


                            appSettings.setString("username", name);
                            appSettings.setString("id", data.getString("id"));

                            if (hasImage) {
                                //   Picasso.get().load(link).placeholder(getResources().getDrawable(R.drawable.circle_gray)).transform(new CircleTransform()).into(profileImage);
                            } else {
                                Thread imageThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                                                R.drawable.icon_uber);
                                        final ImageView userPicture = view.findViewById(R.id.userimage);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    ImagesWorker.setGradient(userPicture, Integer.parseInt(data.getString("id")));
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        });

                                    }
                                });
                                imageThread.start();
                            }


                        } catch (JSONException e) {
                            //TODO: Сделай здесь красивое отображение ошибки
                            e.printStackTrace();
                            // CustomToast.show(getString(R.string.err_json), R.drawable.icon_error, getActivity());
                        }
                    }
                }
            };

            Methods.getMyAccount(appSettings.getString("session"), apiRunnable, getActivity());
        } else {
            if (appSettings.hasKey("profilePhoto")) {
                Picasso.get().load(appSettings.getString("profilePhoto")).networkPolicy(NetworkPolicy.OFFLINE).transform(new CircleTransform()).into(profileImage);
            }
        }
    }
}