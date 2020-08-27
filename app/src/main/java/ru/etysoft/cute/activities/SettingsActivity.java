package ru.etysoft.cute.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrInterface;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.api.APIRunnable;
import ru.etysoft.cute.api.Methods;
import ru.etysoft.cute.bottomsheets.FloatingBottomSheet;
import ru.etysoft.cute.utils.CustomToast;

public class SettingsActivity extends AppCompatActivity implements FloatingBottomSheet.BottomSheetListener {

    //Константы
    private String ISDARK_THEME = "APP_THEME_NIGHT";

    private static AppSettings appSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        appSettings = new AppSettings(this);
        Switch darkSwitch = findViewById(R.id.darkSwitch);
        SlidrInterface slidr = Slidr.attach(this);
        // Проверка и инициализация тёмной темы (для перехода)
        if(appSettings.getBoolean(ISDARK_THEME))
        {
          getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
          darkSwitch.setChecked(true);
        }
        else
        {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Инициализация действия при свиче тёмной темы
        darkSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                buttonView.setEnabled(false);
                changeTheme(buttonView);
            }
        });

        TextView verisonTextView = findViewById(R.id.versionTextView);
        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String version = pInfo.versionName;
            verisonTextView.setText("version: " + version);
        } catch (PackageManager.NameNotFoundException e) {
            verisonTextView.setText("version: unknown");
        }

    }


    public void changePassword(View v) {
        TextView password = findViewById(R.id.password);
        TextView newPassword = findViewById(R.id.newPassword);

        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isSuccess()) {
                            CustomToast.show("Success", R.drawable.icon_success, SettingsActivity.this);
                        }
                    }
                });
            }
        };
        Methods.changePassword(appSettings.getString("session"), password.getText().toString(), newPassword.getText().toString(), apiRunnable, SettingsActivity.this);
    }

    // Показ FloatingBottomSheet
    public void showBSheet(View v) {
        final FloatingBottomSheet bottomSheet = new FloatingBottomSheet();

        bottomSheet.show(getSupportFragmentManager(), "exampleBottomSheet");
        Drawable icon = ContextCompat.getDrawable(this, R.drawable.logo);
        bottomSheet.setCancelable(true);
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheet.getDialog().cancel();
            }
        };

        bottomSheet.setContent(icon, "Test floating", "It is an exaple of floating bottom sheet. I like it. Realy. It is an exaple of floating bottom sheet. I like it. Realy. It is an exaple of floating bottom sheet. I like it. Realy. ", "1", "2", null, onClickListener);
    }


    public void showMeet(View v) {
        Intent intent = new Intent(SettingsActivity.this, Meet.class);
        startActivity(intent);
    }

    public void changeTheme(View v) {
        //Поверка какая тема сейчас стоит
        if (!appSettings.getBoolean(ISDARK_THEME)) {
            appSettings.setBoolean(ISDARK_THEME, true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            appSettings.setBoolean(ISDARK_THEME, false);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Пересоздание активности для красивой смены темы
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

}
