package ru.etysoft.cute;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityOptionsCompat;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private String ISDARK_THEME = "APP_THEME_NIGHT";

    private AppSettings appSettings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        appSettings = new AppSettings(this);
        if(appSettings.getBoolean(ISDARK_THEME))
        {
          getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else
        {
            getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    public void goToSettings(View v)
    {
        Intent i = new Intent(this, Settings.class);
        startActivity(i);
    }

    public void changeTheme(View v)
    {
        if(!appSettings.getBoolean(ISDARK_THEME))
        {
            appSettings.setBoolean(ISDARK_THEME, true);
        }
        else
        {
            appSettings.setBoolean(ISDARK_THEME, false);
        }
        if(appSettings.getBoolean(ISDARK_THEME))
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


}
