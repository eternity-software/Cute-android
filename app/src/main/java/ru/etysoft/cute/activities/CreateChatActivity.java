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
import ru.etysoft.cuteframework.methods.chat.Chat;
import ru.etysoft.cuteframework.methods.chat.Creation.ChatCreateRequest;
import ru.etysoft.cuteframework.methods.chat.Creation.ChatCreateResponse;

public class CreateChatActivity extends AppCompatActivity {


    private SlidrInterface slidr;
    boolean hasChangedToTrans = true;
    private float mPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_conv);
        // Анимация


        SliderActivity sliderActivity = new SliderActivity();
        sliderActivity.attachSlider(this);
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
    }



    public void createConv(View v) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EditText nameView = findViewById(R.id.name);
                    EditText descriptionView = findViewById(R.id.description);
                    ChatCreateResponse chatCreateResponse = (new ChatCreateRequest(CachedValues.getSessionKey(CreateChatActivity.this), String.valueOf(nameView.getText()),
                            String.valueOf(descriptionView.getText()),
                            Chat.Types.CONVERSATION)).execute();
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
                } catch (ResponseException e) {
                    e.printStackTrace();
                } catch (NotCachedException e) {
                    e.printStackTrace();
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