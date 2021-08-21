package ru.etysoft.cute.activities.main;

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
import ru.etysoft.cute.activities.fragments.account.AccountFragment;
import ru.etysoft.cute.activities.fragments.dialogs.DialogsFragment;
import ru.etysoft.cute.activities.fragments.explore.ExploreFragment;
import ru.etysoft.cute.activities.stock;
import ru.etysoft.cute.bottomsheets.FloatingBottomSheet;
import ru.etysoft.cute.services.NotificationService;
import ru.etysoft.cute.utils.Permissions;
import ru.etysoft.cute.utils.ViewPagerAdapter;

public class MainActivity extends AppCompatActivity implements FloatingBottomSheet.BottomSheetListener, MainContract.View {

    public static final boolean isDev = true;

    private MainPresenter mainPresenter;

    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private MenuItem menuItem;
    private DialogsFragment fragmentDialogs;
    private AccountFragment fragmentAccount;
    private ExploreFragment fragmentExplore;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainPresenter = new MainPresenter(this, this);

        startService(new Intent(this, NotificationService.class));

        devOptions();

        setupNavigation();
        Permissions.requestAll(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Permissions.requestAll(this);
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

    @Deprecated
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

    @Override
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
                if (position == 2) {
                    fragmentAccount.updateData();
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

        // Выбрать раздел сообщений
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