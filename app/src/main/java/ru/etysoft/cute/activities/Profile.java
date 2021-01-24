package ru.etysoft.cute.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.ImagesWorker;

public class Profile extends AppCompatActivity {

    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        id = getIntent().getIntExtra("id", -1);
        loadInfo();
        Slidr.attach(this);
    }

    public void loadInfo() {
        AppSettings appSettings = new AppSettings(this);
        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                super.run();
                if (isSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(getResponse());
                        JSONObject data = jsonObject.getJSONObject("data");
                        String name = data.getString("nickname");
                        String status = data.getString("status");
                        String photo = data.getString("photo");
                        int id = data.getInt("id");
                        TextView idv = findViewById(R.id.idview);
                        idv.setText("u" + String.valueOf(id));


                        TextView acronym = findViewById(R.id.acronym);
                        acronym.setText(String.valueOf(name.charAt(0)));

                        ImageView icon = findViewById(R.id.icon);
                        if (photo.equals("null")) {
                            ImagesWorker.setGradient(icon, id);
                        } else {
                            acronym.setVisibility(View.INVISIBLE);
                            Picasso.get().load(Methods.getPhotoUrl(photo + "?size=350")).transform(new CircleTransform()).into(icon);
                        }

                        TextView statusView = findViewById(R.id.status);
                        if (!status.equals("null")) {
                            statusView.setText(status);
                        } else {
                            statusView.setText(getResources().getString(R.string.no_status));
                        }

                        TextView username = findViewById(R.id.username);
                        username.setText(name);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Methods.getAccount(String.valueOf(id), appSettings.getString("session"), apiRunnable, this);
    }


}