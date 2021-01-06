package ru.etysoft.cute.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.StringFormatter;

public class CreateConv extends AppCompatActivity {


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
        AppSettings appSettings = new AppSettings(this);
        TextView nameView = findViewById(R.id.name);
        TextView descView = findViewById(R.id.description);
        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                if (isSuccess()) {
                    CustomToast.show(getString(R.string.create_success), R.drawable.icon_success, CreateConv.this);
                    onBackPressed();
                }
            }
        };
        String formattedName = StringFormatter.format(nameView.getText().toString());
        Methods.createConversations(appSettings.getString("session"), "public", formattedName, descView.getText().toString(), apiRunnable, this);
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