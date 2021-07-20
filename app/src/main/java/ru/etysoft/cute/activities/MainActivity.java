package ru.etysoft.cute.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.meet.MeetActivity;
import ru.etysoft.cute.bottomsheets.FloatingBottomSheet;
import ru.etysoft.cute.data.CacheUtils;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.fragments.account.AccountFragment;
import ru.etysoft.cute.fragments.dialogs.DialogsFragment;
import ru.etysoft.cute.fragments.explore.ExploreFragment;
import ru.etysoft.cute.services.NotificationService;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.ErrorCodes;
import ru.etysoft.cute.utils.Logger;
import ru.etysoft.cute.utils.Permissions;
import ru.etysoft.cute.utils.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements FloatingBottomSheet.BottomSheetListener {

    public static final boolean isDev = true;


    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private MenuItem menuItem;
    private DialogsFragment fragmentDialogs;
    private AccountFragment fragmentAccount;
    private ExploreFragment fragmentExplore;
    private CacheUtils cacheUtils;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            if (item.getItemId() == R.id.explore) {
                viewPager.setCurrentItem(0);
            } else if (item.getItemId() == R.id.dialogs) {
                viewPager.setCurrentItem(1);
            } else if (item.getItemId() == R.id.account) {
                viewPager.setCurrentItem(2);
            }
            return false;
        }
    };


    private boolean isbanned = false;
    public static boolean amIcreated = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permissions.checkAvailble(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Logger.logActivity("Created Main");
        startService(new Intent(this, NotificationService.class));

        cacheUtils = CacheUtils.getInstance();

        // Инициализация кодов ошибок
        ErrorCodes.initialize(this);

        // Проверка сессии
        try {
            if (CachedValues.getSessionKey(this) == null) {
                Intent intent = new Intent(MainActivity.this, MeetActivity.class);
                CacheUtils cacheUtils = CacheUtils.getInstance();
                cacheUtils.clean(this);
                startActivity(intent);
                finish();
                return;
            }
        } catch (NotCachedException e) {
            e.printStackTrace();
        }


        // Инициализация навигации
        setupNavigation();
        Permissions.checkAvailble(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void checksAPI() {
        checkAccount();
    }

    public void checkAccount() {
        final String session;
        try {
            session = CachedValues.getSessionKey(this);

            if (session != null) {
            } else {
                if (!isbanned) {
                    Intent intent = new Intent(MainActivity.this, MeetActivity.class);
                    CacheUtils cacheUtils = CacheUtils.getInstance();
                    cacheUtils.clean(this);
                    startActivity(intent);
                    CustomToast.show("Session error", R.drawable.icon_error, MainActivity.this);
                    finish();
                }
            }
        } catch (NotCachedException e) {
            e.printStackTrace();
        }
    }

    public void devOptions() {
        final FloatingBottomSheet floatingBottomSheet = new FloatingBottomSheet();

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, stock.class);
                startActivity(intent);
                floatingBottomSheet.dismiss();
            }
        };

        floatingBottomSheet.setContent(getResources().getDrawable(R.drawable.logo), "Режим разработчика", "Вы можете открыть дополнительное меню разработчика.", "ОТКРЫТЬ", null, onClickListener, null);
        floatingBottomSheet.show(getSupportFragmentManager(), "blocked");
        floatingBottomSheet.setCancelable(true);

    }


    public void setupNavigation() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.nav_view);
        viewPager = (ViewPager) findViewById(R.id.view);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                menuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        setupViewPager(viewPager);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.dialogs);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // Выбрать диалоги
        viewPager.setCurrentItem(1);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        fragmentExplore = new ExploreFragment();
        fragmentDialogs = new DialogsFragment();
        fragmentAccount = new AccountFragment();
        viewPagerAdapter.addFragment(fragmentExplore);
        viewPagerAdapter.addFragment(fragmentDialogs);
        viewPagerAdapter.addFragment(fragmentAccount);
        viewPager.setSaveEnabled(true);
        viewPager.setSaveFromParentEnabled(true);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(viewPagerAdapter);
    }
}
