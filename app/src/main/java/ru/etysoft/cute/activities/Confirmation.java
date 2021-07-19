package ru.etysoft.cute.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.meet.MeetActivity;
import ru.etysoft.cute.utils.CustomToast;

public class Confirmation extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);
        // Анимация
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        AppSettings appSettings = new AppSettings(this);

        TextView email = findViewById(R.id.emailto);
        email.setText(appSettings.getString("email"));
    }

    public void back(View v) {
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        Intent intent = new Intent(Confirmation.this, MeetActivity.class);
        startActivity(intent);
    }

    public void confirmButton(View v) {
        TextView codeView = findViewById(R.id.confirmationCode);

        final AppSettings appSettings = new AppSettings(getApplicationContext());
        final String code = codeView.getText().toString();


    }

    public void sendAnotherConfirmationCode(View v) {

    }


    public void cantReceive(View v) {
        Uri address = Uri.parse("https://pony.town/");
        Intent openLinkIntent = new Intent(Intent.ACTION_VIEW, address);

        if (openLinkIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(openLinkIntent);
        } else {
            CustomToast.show("Error!", R.drawable.icon_error, Confirmation.this);
        }


    }

    @Override
    public void onBackPressed() {


    }
}
