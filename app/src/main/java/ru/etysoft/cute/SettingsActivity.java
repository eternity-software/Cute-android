package ru.etysoft.cute;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

public class SettingsActivity extends AppCompatActivity {

    //Константы
    private String ISDARK_THEME = "APP_THEME_NIGHT";

    private static AppSettings appSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        appSettings = new AppSettings(this);
        Switch darkSwitch = findViewById(R.id.darkSwitch);

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
    }





    public void changeTheme(View v)
    {
        //Поверка какая тема сейчас стоит
        if(!appSettings.getBoolean(ISDARK_THEME))
        {
            appSettings.setBoolean(ISDARK_THEME, true);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else
        {
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
