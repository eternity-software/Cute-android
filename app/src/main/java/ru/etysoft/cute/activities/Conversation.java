package ru.etysoft.cute.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.conversation.ConversationAdapter;
import ru.etysoft.cute.activities.conversation.ConversationInfo;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.Numbers;

public class Conversation extends AppCompatActivity {

    private List<ConversationInfo> convInfos = new ArrayList<>();
    private Map<String, ConversationInfo> ids = new HashMap<String, ConversationInfo>();

    private String cid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        cid = getIntent().getStringExtra("cid");
        // Анимация
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        updateList();
        Slidr.attach(this);
    }

    public void back(View v) {
        onBackPressed();
    }

    public void leave(View v) {
        AppSettings appSettings = new AppSettings(this);

        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                if (isSuccess()) {
                    finish();
                }
            }
        };

        Methods.leaveConversation(appSettings.getString("session"), cid, apiRunnable, this);
    }


    public void updateList() {
        final ListView listView = findViewById(R.id.messages);
        AppSettings appSettings = new AppSettings(this);

        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                if (isSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(getResponse());
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject message = data.getJSONObject(i);

                            final String id = message.getString("id");
                            final String aid = message.getString("aid");
                            final String text = message.getString("text");
                            String time = message.getString("time");
                            final String name = message.getString("name");
                            final int mine = message.getInt("my");
                            final int read = message.getInt("readed");
                            boolean my;
                            boolean readed;
                            if (mine == 1) {
                                my = true;
                            } else {
                                my = false;
                            }

                            if (read == 1) {
                                readed = true;

                            } else {
                                readed = false;

                            }

                            if (!ids.containsKey(id)) {
                                ConversationInfo conversationInfo = new ConversationInfo(id, name, text, my, false, Numbers.getTimeFromTimestamp(time, getApplicationContext()), readed, aid);
                                ids.put(id, conversationInfo);
                                convInfos.add(conversationInfo);
                            } else {
                                ConversationInfo conversationInfo = ids.get(id);
                                if (conversationInfo.isReaded() != readed) {
                                    conversationInfo.setReaded(readed);
                                }
                            }
                        }
                        ConversationAdapter adapter = new ConversationAdapter(Conversation.this, convInfos);
                        listView.setAdapter(adapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomToast.show(getString(R.string.err_json), R.drawable.icon_error, Conversation.this);
                    }
                }
            }
        };

        Methods.getMessages(appSettings.getString("session"), cid, apiRunnable, this);
    }


    public void sendMessage(View v) {
        final TextView messageBox = findViewById(R.id.message_box);
        final ListView listView = findViewById(R.id.messages);
        AppSettings appSettings = new AppSettings(this);
        final ConversationInfo conversationInfo = new ConversationInfo("null", "s", messageBox.getText().toString(), true, false, "Sending...", false, "null");
        convInfos.add(conversationInfo);
        ConversationAdapter adapter = new ConversationAdapter(this, convInfos);
        listView.setAdapter(adapter);

        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                if (isSuccess()) {
                    try {
                        JSONObject jsonObject = new JSONObject(getResponse());

                        JSONObject infomessage = jsonObject.getJSONObject("data");
                        String id = infomessage.getString("id");
                        String time = infomessage.getString("time");
                        String message = infomessage.getString("text");

                        ids.put(id, conversationInfo);

                        conversationInfo.setLastmessage(message);
                        conversationInfo.setId(id);
                        conversationInfo.setSubtext(Numbers.getTimeFromTimestamp(time, getApplicationContext()));
                        ConversationAdapter adapter = new ConversationAdapter(Conversation.this, convInfos);
                        listView.setAdapter(adapter);
                        updateList();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        CustomToast.show(getString(R.string.err_json), R.drawable.icon_error, Conversation.this);
                    }

                } else {
                    conversationInfo.setSubtext("Error");
                    ConversationAdapter adapter = new ConversationAdapter(Conversation.this, convInfos);
                    listView.setAdapter(adapter);
                }
            }
        };

        Methods.sendTextMessage(appSettings.getString("session"), messageBox.getText().toString(), cid, apiRunnable, this);


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}