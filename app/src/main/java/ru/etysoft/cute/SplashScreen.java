package ru.etysoft.cute;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ProcessLifecycleOwner;

import ru.etysoft.cute.activities.main.MainActivity;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.data.CacheUtils;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.LanguageParsingException;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.lang.CustomLanguage;
import ru.etysoft.cute.themes.Theme;
import ru.etysoft.cute.utils.SocketHolder;

public class SplashScreen extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        try {
            Theme.loadExisting(this);
        } catch (NotCachedException ignored) {
        } catch (LanguageParsingException e) {
            CuteToast.show(getResources().getString(R.string.err_theme_url), R.drawable.icon_error, this);
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SocketHolder.initialize(CachedValues.getSessionKey(SplashScreen.this));

                } catch (NotCachedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

        try {
            CustomLanguage.loadExisting(this);
        } catch (NotCachedException ignored) {
        } catch (LanguageParsingException e) {
            CuteToast.show(getResources().getString(R.string.err_lang), R.drawable.icon_error, this);
        }

        Theme.initTheme(this);
        // Запуск активности
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();

        // Инициализация текущей темы и её применение

    }
}