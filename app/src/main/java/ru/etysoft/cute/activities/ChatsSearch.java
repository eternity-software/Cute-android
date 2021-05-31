package ru.etysoft.cute.activities;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.chatsearch.ChatSearchAdapter;
import ru.etysoft.cute.activities.chatsearch.ChatSearchInfo;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;

public class ChatsSearch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats_search);
        Slidr.attach(this);
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);


        final EditText searchBox = findViewById(R.id.searchBox);
        searchBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                search(String.valueOf(searchBox.getText()));
                return false;
            }
        });

    }


    public void search(String query) {
        AppSettings appSettings = new AppSettings(this);
        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                if (isSuccess()) {
                    List<ChatSearchInfo> values = new ArrayList<>();
                    try {
                        ListView listView = findViewById(R.id.results);
                        JSONObject response = new JSONObject(getResponse());
                        JSONArray data = response.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject object = data.getJSONObject(i);
                            int cid = Integer.parseInt(object.getString("id"));
                            boolean has = (Integer.parseInt(object.getString("mine")) == 1);
                            int countmembers = Integer.parseInt(object.getString("count_members"));
                            String name = object.getString("name");
                            values.add(new ChatSearchInfo(cid, name, countmembers, has));
                        }
                        ChatSearchAdapter chatSearchAdapter = new ChatSearchAdapter(ChatsSearch.this, values);
                        listView.setAdapter(chatSearchAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        Methods.searchChat(appSettings.getString("session"), query, apiRunnable, this);
    }

    public void back(View v) {
        onBackPressed();
    }
}