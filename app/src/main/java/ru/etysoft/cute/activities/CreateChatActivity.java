package ru.etysoft.cute.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.model.SlidrInterface;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cuteframework.exceptions.ResponseException;

import ru.etysoft.cuteframework.methods.chat.ChatCreateRequest;
import ru.etysoft.cuteframework.models.Chat;
import ru.etysoft.cuteframework.storage.Cache;

public class CreateChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_conv);


        SliderActivity sliderActivity = new SliderActivity();
        sliderActivity.attachSlider(this);
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        System.out.println("cache usage " + Cache.getSizeMb());

    }



    public void createConv(View v) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EditText nameView = findViewById(R.id.name);
                    EditText descriptionView = findViewById(R.id.description);
                    ChatCreateRequest.ChatCreateResponse chatCreateResponse = new ChatCreateRequest(
                            Chat.TYPE_CONVERSATION,
                            nameView.getText().toString(),
                            descriptionView.getText().toString()).execute();
                    if (chatCreateResponse.isSuccess()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CuteToast.showSuccess(getResources().getString(R.string.chat_create_success), CreateChatActivity.this);
                                finish();
                            }
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                CuteToast.showError(getResources().getString(R.string.err_unknown), CreateChatActivity.this);
                            }
                        });

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }


    public void back(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }
}