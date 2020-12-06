package ru.etysoft.cute.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.ImageView;
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
import ru.etysoft.cute.activities.dialogs.DialogAdapter;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.bottomsheets.conversation.ConversationBottomSheet;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.ImagesWorker;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cute.utils.SendorsControl;

public class Conversation extends AppCompatActivity implements ConversationBottomSheet.BottomSheetListener {

    private List<ConversationInfo> convInfos = new ArrayList<>();
    private Map<String, ConversationInfo> ids = new HashMap<String, ConversationInfo>();

    private String cid = "1";
    private String name = "42";
    private boolean isDialog = false;


    public boolean isVoice = true;

    @Override
    protected void onResume() {
        super.onResume();

    }

    public void back(View v) {
        onBackPressed();
    }


    public void showInfo(View v) {
        final ConversationBottomSheet conversationBottomSheet = new ConversationBottomSheet();
        conversationBottomSheet.setCid(cid);
        conversationBottomSheet.show(getSupportFragmentManager(), "blocked");
        conversationBottomSheet.setCancelable(true);
    }


    // Обновляем список сообщений
    public void updateList() {
        final ListView listView = findViewById(R.id.messages);
        AppSettings appSettings = new AppSettings(this);

        // Задаём обработчик ответа API
        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                if (isSuccess()) {
                    try {

                        // Обработка ответа JSON
                        JSONObject jsonObject = new JSONObject(getResponse());
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject message = data.getJSONObject(i);

                            final String id = message.getString("id");
                            final int aid = message.getInt("aid");
                            final String text = message.getString("text");
                            String time = message.getString("time");
                            final String name = message.getString("name");
                            final int mine = message.getInt("my");
                            final int read = message.getInt("readed");

                            boolean my;
                            boolean readed;
                            boolean isonline;

                            my = Numbers.getBooleanFromInt(mine);

                            readed = Numbers.getBooleanFromInt(read);


                            boolean isInfo = false;
                            if (aid == -1) {
                                isInfo = true;
                            }


                            // Если это id уже есть, то проверяем прочитанность, а если нет, то добавляем
                            if (!ids.containsKey(id)) {
                                ConversationInfo conversationInfo = new ConversationInfo(id, name, text, my, false, Numbers.getTimeFromTimestamp(time, getApplicationContext()), readed, aid, isInfo);
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


    // Отправляем сообщение
    public void sendMessage(View v) {
        final TextView messageBox = findViewById(R.id.message_box);
        final ListView listView = findViewById(R.id.messages);
        AppSettings appSettings = new AppSettings(this);
        final ConversationInfo conversationInfo = new ConversationInfo("null", "s", messageBox.getText().toString(), true, false, getString(R.string.sending), false, -10, false);
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

                        conversationInfo.setMessage(message);
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
                    conversationInfo.setSubtext(getString(R.string.err_not_sended));
                    ConversationAdapter adapter = new ConversationAdapter(Conversation.this, convInfos);
                    listView.setAdapter(adapter);
                }
            }
        };

        Methods.sendTextMessage(appSettings.getString("session"), messageBox.getText().toString(), cid, apiRunnable, this);
        messageBox.setText("");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DialogAdapter.canOpen = true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        cid = getIntent().getStringExtra("cid");
        name = getIntent().getStringExtra("name");
        isDialog = getIntent().getBooleanExtra("isd", false);

        ImageView picture = findViewById(R.id.icon);
        ImagesWorker.setGradient(picture, Integer.parseInt(cid));

        TextView acronym = findViewById(R.id.acronym);
        acronym.setVisibility(View.VISIBLE);
        acronym.setText(String.valueOf(name.charAt(0)));

        TextView subtitle = findViewById(R.id.subtitle);
        if (!isDialog) {
            subtitle.setText("0 members");
        }

        TextView title = findViewById(R.id.title);
        title.setText(name);
        setupVoiceButton();
        updateList();
        Slidr.attach(this);
        setupOnTextInput();

    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupVoiceButton() {
        final ImageView microphone = findViewById(R.id.sendVoice);
        microphone.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final float max = 2.0f;
                final float pivotX = 0.7f;
                final float pivotY = 0.7f;
                final int duration = 150;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        SendorsControl.vibrate(getApplicationContext(), 100);
                        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, max, 1.0f, max, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
                        scaleAnimation.setDuration(duration);


                        scaleAnimation.setFillAfter(true);

                        microphone.startAnimation(scaleAnimation);
                        break;
                    case MotionEvent.ACTION_MOVE: // движение
                        break;
                    case MotionEvent.ACTION_UP:
                        ScaleAnimation dscaleAnimation = new ScaleAnimation(max, 1.0f, max, 1.0f, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
                        dscaleAnimation.setDuration(duration);
                        dscaleAnimation.setFillAfter(true);

                        microphone.startAnimation(dscaleAnimation);// отпускание
                    case MotionEvent.ACTION_CANCEL:

                        break;
                }
                return true;


            }
        });

    }

    public void setupOnTextInput() {
        final ImageView sendBtn = findViewById(R.id.sendButton);
        final ImageView voiceBtn = findViewById(R.id.sendVoice);
        final EditText editText = findViewById(R.id.message_box);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int duration = 150;
                float pivotX = 0.5f;
                float pivotY = 0.5f;
                ScaleAnimation decreaseAnimation = new ScaleAnimation(1.0f, 0, 1.0f, 0, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
                decreaseAnimation.setDuration(duration);
                decreaseAnimation.setFillAfter(true);

                ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1.0f, 0, 1.0f, Animation.RELATIVE_TO_SELF, pivotX, Animation.RELATIVE_TO_SELF, pivotY);
                scaleAnimation.setDuration(duration);
                scaleAnimation.setFillAfter(true);
                if (String.valueOf(editText.getText()).equals("")) {
                    isVoice = true;
                    voiceBtn.startAnimation(scaleAnimation);
                    sendBtn.startAnimation(decreaseAnimation);
                    sendBtn.setEnabled(false);
                    voiceBtn.setEnabled(true);
                } else {
                    if (isVoice == true) {
                        isVoice = false;
                        sendBtn.setEnabled(true);
                        voiceBtn.setEnabled(false);
                        voiceBtn.startAnimation(decreaseAnimation);
                        sendBtn.startAnimation(scaleAnimation);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //  overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}