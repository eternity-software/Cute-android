package ru.etysoft.cute;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import ru.etysoft.cute.activities.MainActivity;
import ru.etysoft.cute.api.Methods;

public class SplashScreen extends AppCompatActivity {
    private final static String ISDARK_THEME = "APP_THEME_NIGHT";

    private AppSettings appSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Methods.initialize(this);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        appSettings = new AppSettings(this);

        // Запуск активности
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();

        // Инициализация текущей темы и её применение
        if (appSettings.hasKey(ISDARK_THEME)) {
            if (appSettings.getBoolean(ISDARK_THEME)) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
    }
}