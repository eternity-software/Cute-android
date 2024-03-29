package ru.etysoft.cute.activities;

import android.app.Activity;
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

import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrPosition;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.meet.MeetActivity;
import ru.etysoft.cute.bottomsheets.FloatingBottomSheet;
import ru.etysoft.cute.data.CacheUtils;
import ru.etysoft.cute.lang.CustomLanguage;
import ru.etysoft.cute.services.NotificationService;

public class stock extends AppCompatActivity implements FloatingBottomSheet.BottomSheetListener {

    private static CacheUtils cacheUtils;
    private static stock main;
    public Activity parent;
    //Константы
    private final String ISDARK_THEME = "APP_THEME_NIGHT";

    public static stock get() {
        return main;
    }

    public void changePassword(View v) {
        TextView password = findViewById(R.id.password);
        TextView newPassword = findViewById(R.id.newPassword);
        CustomLanguage.loadFromUrl(String.valueOf(password.getText()), this, false);
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
               // notifyBanner(getApplicationContext(), "fuck");
            }
        };

        View.OnClickListener delete = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cacheUtils.setString(NotificationService.KEY, "Clean history", getApplicationContext());
            }
        };

        bottomSheet.setContent(icon, "Test floating", "It is an exaple of floating bottom sheet. I like it. Realy. It is an exaple of floating bottom sheet. I like it. Realy. It is an exaple of floating bottom sheet. I like it. Realy. ", "1", "2", delete, onClickListener);
    }


    public void showMeet(View v) {
        Intent intent = new Intent(stock.this, MeetActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        main = this;
        cacheUtils = CacheUtils.getInstance();
        setContentView(R.layout.activity_settings);


        Switch darkSwitch = findViewById(R.id.darkSwitch);
        SlidrConfig config = new SlidrConfig.Builder()
                .position(SlidrPosition.BOTTOM)
                .build();
        //Slidr.attach(this, config);
        // Проверка и инициализация тёмной темы (для перехода)
        if (cacheUtils.getBoolean(ISDARK_THEME, this)) {
            if (getDelegate().getLocalNightMode() != AppCompatDelegate.MODE_NIGHT_YES) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            darkSwitch.setChecked(true);
        } else {

            if (getDelegate().getLocalNightMode() != AppCompatDelegate.MODE_NIGHT_NO) {
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
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

        TextView logView = findViewById(R.id.logNotif);
        if (cacheUtils.hasKey(NotificationService.KEY, this)) {
            logView.setText(cacheUtils.getString(NotificationService.KEY, this));
        }


    }


    public void changeTheme(View v) {
        //Поверка какая тема сейчас стоит
        if (!cacheUtils.getBoolean(ISDARK_THEME, this)) {
            cacheUtils.setBoolean(ISDARK_THEME, true, this);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            cacheUtils.setBoolean(ISDARK_THEME, false, this);
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        // Пересоздание активности для красивой смены темы
        Intent intent = new Intent(stock.this, stock.class);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

}
