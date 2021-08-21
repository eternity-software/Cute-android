package ru.etysoft.cute.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import org.json.JSONException;

import ru.etysoft.cute.R;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.chat.Chat;
import ru.etysoft.cuteframework.methods.chat.Creation.ChatCreateRequest;
import ru.etysoft.cuteframework.methods.chat.Creation.ChatCreateResponse;

public class CreateChatActivity extends AppCompatActivity {


    private SlidrInterface slidr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_conv);
        // Анимация

        Slidr.attach(this);
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
                                finish();
                            }
                        });

                    }
                } catch (ResponseException e) {
                    e.printStackTrace();
                } catch (NotCachedException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
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