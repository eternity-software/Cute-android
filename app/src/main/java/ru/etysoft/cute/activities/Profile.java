package ru.etysoft.cute.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.friend.Remove.RemoveFriendRequest;
import ru.etysoft.cuteframework.methods.friend.Remove.RemoveFriendResponse;

import ru.etysoft.cuteframework.methods.friend.SendRequest.AddFriendRequest;
import ru.etysoft.cuteframework.methods.friend.SendRequest.AddFriendRequestResponse;
import ru.etysoft.cuteframework.methods.user.Get.GetUserRequest;
import ru.etysoft.cuteframework.methods.user.Get.GetUserResponse;

public class Profile extends AppCompatActivity {

    private long id;
    private String url;
    private String coverUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        id = getIntent().getLongExtra("id", -1);
        loadData();
        Slidr.attach(this);
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
    }



    public void loadData() {
        final Avatar avatar = findViewById(R.id.icon);
        final ImageView coverView = findViewById(R.id.coverView);
        final ImageView backgroundView = findViewById(R.id.backgroundView);
        backgroundView.setBackgroundColor(Numbers.getColorById(id, this));
        avatar.generateIdPicture(id);
        Thread loadInfo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final GetUserResponse getUserResponse = (new GetUserRequest(CachedValues.getSessionKey(Profile.this), String.valueOf(id))).execute();
                    if(getUserResponse.isSuccess()) {
                        final String name = getUserResponse.getUser().getDisplayName();
                        final String bio = getUserResponse.getUser().getBioText();
                        final String status = getUserResponse.getUser().getStatusText();
                        url = getUserResponse.getUser().getAvatar();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView displayName = findViewById(R.id.displayNameView);
                                TextView statusView = findViewById(R.id.statusView);
                                TextView bioView = findViewById(R.id.bioView);

                                final ImageButton friendButton = findViewById(R.id.friendButton);

                                if(getUserResponse.getUser().isFriend())
                                {

                                    friendButton.setImageDrawable(getResources().getDrawable(R.drawable.icon_remove_person));
                                    friendButton.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Thread removeThread = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        RemoveFriendResponse removeFriendResponse = (new RemoveFriendRequest(CachedValues.getSessionKey(getApplicationContext()),
                                                                String.valueOf(getUserResponse.getFriendId()))).execute();
                                                        if(removeFriendResponse.isSuccess())
                                                        {
                                                            friendButton.setImageDrawable(getResources().getDrawable(R.drawable.icon_add));
                                                            friendButton.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View v) {
                                                                    addFriend(v);
                                                                }
                                                            });
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            removeThread.start();
                                        }
                                    });
                                }

                                displayName.setText(name);
                                avatar.setAcronym(name, Avatar.Size.LARGE);
                                Picasso.get().load(getUserResponse.getUser().getAvatar()).transform(new CircleTransform()).into(avatar.getPictureView());

                                    coverUrl = getUserResponse.getUser().getCover();
                                    Picasso.get().load(getUserResponse.getUser().getCover()).into(coverView);

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
        if(url != null) {
            Intent intent = new Intent(Profile.this, ImagePreview.class);
            intent.putExtra("url", url);
            startActivity(intent);
        }
    }

    public void addFriend(View v)
    {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final ImageButton friendButton = findViewById(R.id.friendButton);
                    friendButton.setImageDrawable(getResources().getDrawable(R.drawable.icon_remove_person));
                    AddFriendRequestResponse sendFriendRequestResponse = (new AddFriendRequest(CachedValues.getSessionKey(getApplicationContext()), String.valueOf(id))).execute();
                    if(sendFriendRequestResponse.isSuccess())
                    {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CuteToast.showSuccess("Success", Profile.this);
                            }
                        });
                    }
                } catch (ResponseException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    public void openCover(View v) {
        if(coverUrl != null)
        {
            Intent intent = new Intent(Profile.this, ImagePreview.class);
            intent.putExtra("url", coverUrl);
            startActivity(intent);
        }

    }

}