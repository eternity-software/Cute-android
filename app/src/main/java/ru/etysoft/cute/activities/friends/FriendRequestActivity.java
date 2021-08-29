package ru.etysoft.cute.activities.friends;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;

import ru.etysoft.cute.R;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cuteframework.methods.friend.Friend;
import ru.etysoft.cuteframework.methods.friend.GetRequests.FriendRequestsResponse;
import ru.etysoft.cuteframework.methods.friend.GetRequests.GetFriendRequests;

public class FriendRequestActivity extends AppCompatActivity {
    private FriendRequestAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        adapter = new FriendRequestAdapter(this, new ArrayList<Friend>());
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        loadFriends();
        SliderActivity sliderActivity = new SliderActivity();
        sliderActivity.attachSlider(this);
    }

    public void loadFriends()
    {
        Thread processServerRequest = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FriendRequestsResponse friendRequestsResponse = (new GetFriendRequests(CachedValues.getSessionKey(FriendRequestActivity.this))).execute();
                    if(friendRequestsResponse.isSuccess())
                    {
                        for(final Friend friend : friendRequestsResponse.getRequests())
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.add(friend);
                                }
                            });
                        }
                    }
                }
                catch (Exception e)
                {

                }
            }
        });
        processServerRequest.start();
    }
}