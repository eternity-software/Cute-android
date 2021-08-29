package ru.etysoft.cute.activities.friends;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import ru.etysoft.cute.R;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cuteframework.methods.friend.Friend;
import ru.etysoft.cuteframework.methods.friend.GetFriends.FriendListRequest;
import ru.etysoft.cuteframework.methods.friend.GetFriends.FriendListResponse;

public class FriendsActivity extends AppCompatActivity {

    private FriendsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        adapter = new FriendsAdapter(this, new ArrayList<Friend>());
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
        loadFriends();
        SliderActivity sliderActivity = new SliderActivity();
        sliderActivity.attachSlider(this);
    }

    public void openRequests(View v)
    {

                Intent intent = new Intent(this, FriendRequestActivity.class);
                startActivity(intent);

    }

    public void loadFriends()
    {
        Thread processServerRequest = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FriendListResponse friendListResponse = (new FriendListRequest(CachedValues.getSessionKey(FriendsActivity.this))).execute();
                    if(friendListResponse.isSuccess())
                    {
                        for(final Friend friend : friendListResponse.getFriends())
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