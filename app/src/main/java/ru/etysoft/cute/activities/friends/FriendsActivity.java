package ru.etysoft.cute.activities.friends;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.LightToolbar;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.friend.Friend;
import ru.etysoft.cuteframework.methods.friend.GetFriends.FriendListRequest;
import ru.etysoft.cuteframework.methods.friend.GetFriends.FriendListResponse;
import ru.etysoft.cuteframework.methods.friend.GetRequests.FriendRequestsResponse;
import ru.etysoft.cuteframework.methods.friend.GetRequests.GetIncomingFriendRequests;
import ru.etysoft.cuteframework.methods.user.User;

public class FriendsActivity extends AppCompatActivity {

    private FriendsAdapter adapter;
    private TextView friendsCountView;
    private String response;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        adapter = new FriendsAdapter(this, new ArrayList<User>());
        ListView listView = findViewById(R.id.listView);
        friendsCountView = findViewById(R.id.friendsCountView);

        LightToolbar lightToolbar = findViewById(R.id.lightToolbar);
        lightToolbar.animateAppear(findViewById(R.id.toolbarContainer));
        listView.setAdapter(adapter);
        loadFriends();
        SliderActivity sliderActivity = new SliderActivity();
        sliderActivity.attachSlider(this);
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
    }

    public void openRequests(View v)
    {
                Intent intent = new Intent(this, FriendRequestActivity.class);
                intent.putExtra("response", response);
                startActivity(intent);
    }

    public void loadFriends()
    {
        Thread processServerRequest = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final FriendListResponse friendListResponse = (new FriendListRequest(CachedValues.getSessionKey(FriendsActivity.this))).execute();
                    if(friendListResponse.isSuccess())
                    {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        adapter.addAll(friendListResponse.getFriends());
                                        friendsCountView.setText(String.valueOf(friendListResponse.getFriends().size()));
                                    } catch (ResponseException e) {
                                        e.printStackTrace();
                                    }

                                }
                            });

                    }

                    final FriendRequestsResponse friendRequestsResponse = (new GetIncomingFriendRequests(CachedValues.getSessionKey(FriendsActivity.this))).execute();
                    response = friendRequestsResponse.getJsonResponse().toString();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                              TextView incomingRequestsView = findViewById(R.id.requestCountView);
                              incomingRequestsView.setText("+" + friendRequestsResponse.getRequests().size());
                            } catch (ResponseException e) {
                                e.printStackTrace();
                            }

                        }
                    });
                }
                catch (Exception e)
                {

                }
            }
        });
        processServerRequest.start();
    }
}