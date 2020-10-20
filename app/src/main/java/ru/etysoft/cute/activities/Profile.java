package ru.etysoft.cute.activities;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;

import org.json.JSONObject;

import ru.etysoft.cute.R;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
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
        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                super.run();
                if (isSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(getResponse());
                        JSONObject data = jsonObject.getJSONObject("data");
                        String name = data.getString("nickname");
                        int id = data.getInt("id");
                        TextView idv = findViewById(R.id.idview);
                        idv.setText(String.valueOf(id));

                        TextView username = findViewById(R.id.username);
                        username.setText(name);

                        TextView acronym = findViewById(R.id.acronym);
                        acronym.setText(String.valueOf(name.charAt(0)));

                        ImageView picture = findViewById(R.id.picture);
                        ImagesWorker.setGradient(picture, id);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Methods.getAccount(String.valueOf(id), apiRunnable, this);
    }


}