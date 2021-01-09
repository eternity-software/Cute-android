package ru.etysoft.cute.fragments.account;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import ru.etysoft.cute.utils.ImagesWorker;
import ru.etysoft.cute.utils.Numbers;

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

        final AppSettings appSettings = new AppSettings(getActivity());
        TextView id = root.findViewById(R.id.idview);
        TextView username = root.findViewById(R.id.username);
        if (appSettings.getString("username") != null) {
            id.setText("u" + appSettings.getString("id"));
            username.setText(appSettings.getString("username"));
        }


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
                            final JSONObject data = jsonObject.getJSONObject("data");

                            final TextView id = view.findViewById(R.id.idview);
                            id.setText("u" + data.getString("id"));
                            boolean hasImage = false;
                            TextView username = view.findViewById(R.id.username);
                            username.setText(data.getString("nickname"));

                            appSettings.setString("username", data.getString("nickname"));
                            appSettings.setString("id", data.getString("id"));

                            if (hasImage) {
                                Thread imageThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                                                R.drawable.icon_uber);
                                        final ImageView userPicture = view.findViewById(R.id.userimoage);
                                        int px = (int) Numbers.dpToPx(80, getContext());
                                        final Bitmap b = ImagesWorker.getCircleCroppedBitmap(icon, px, px);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                userPicture.setImageBitmap(b);
                                            }
                                        });

                                    }
                                });
                                imageThread.run();
                            } else {
                                Thread imageThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Bitmap icon = BitmapFactory.decodeResource(getActivity().getResources(),
                                                R.drawable.icon_uber);
                                        final ImageView userPicture = view.findViewById(R.id.userimoage);
                                        try {
                                            ImagesWorker.setGradient(userPicture, Integer.parseInt(data.getString("id")));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                                imageThread.run();
                            }


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