package ru.etysoft.cute.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.Picasso;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.user.Get.GetUserRequest;
import ru.etysoft.cuteframework.methods.user.Get.GetUserResponse;

public class Profile extends AppCompatActivity {

    private int id;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        id = getIntent().getIntExtra("id", -1);
        loadData();
        Slidr.attach(this);
    }

    public void loadData() {
        final Avatar avatar = findViewById(R.id.icon);
        avatar.generateIdPicture(id);
        Thread loadInfo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final GetUserResponse getUserResponse = (new GetUserRequest(CachedValues.getSessionKey(Profile.this), String.valueOf(id))).execute();
                    if(getUserResponse.isSuccess()) {
                        final String name = getUserResponse.getDisplayName();
                        final String bio = getUserResponse.getBio();
                        final String status = getUserResponse.getStatus();
                        url = getUserResponse.getAvatarPath();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView displayName = findViewById(R.id.displayNameView);
                                TextView statusView = findViewById(R.id.statusView);
                                TextView bioView = findViewById(R.id.bioView);
                                displayName.setText(name);
                                avatar.setAcronym(name);
                                Picasso.get().load(getUserResponse.getAvatarPath()).transform(new CircleTransform()).into(avatar.getPictureView());
                                statusView.setText(status);
                                bioView.setText(bio);
                            }
                        });
                    }
                    else
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        });
                    }
                } catch (NotCachedException | ResponseException e) {
                    e.printStackTrace();
                }
                catch (final Exception e)
                {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            CuteToast.showError(e.getMessage(), Profile.this);
                        }
                    });

                }
            }
        });
        loadInfo.start();
    }

    public void openImage(View v) {
        Intent intent = new Intent(Profile.this, ImagePreview.class);
        intent.putExtra("url", url);
        startActivity(intent);
    }

}