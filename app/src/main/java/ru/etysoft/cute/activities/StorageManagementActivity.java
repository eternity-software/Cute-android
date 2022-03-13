package ru.etysoft.cute.activities;


import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;

import java.sql.SQLException;

import ru.etysoft.cute.R;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cuteframework.storage.Cache;

public class StorageManagementActivity extends AppCompatActivity {

    private TextView summaryStorageUsageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_storage_management);
        initializeViews();
        Slidr.attach(this);
        summaryStorageUsageView.setText(Cache.getSizeMb() + " MB");
    }

    public void initializeViews()
    {
        summaryStorageUsageView = findViewById(R.id.storageUsageSummaryView);
    }

    public void cleanCache(View view)
    {
        try {
            Cache.getChatSnippetsTable().clean();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}