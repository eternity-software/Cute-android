package ru.etysoft.cute.activities.friends;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.r0adkll.slidr.model.SlidrInterface;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.LightToolbar;
import ru.etysoft.cute.lang.StringsRepository;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cuteframework.methods.friend.GetRequests.FriendRequestsResponse;

public class FriendRequestActivity extends AppCompatActivity {


    private SlidrInterface slidrInterface;
    private TextView requestCountView;
    private FriendRequestsResponse friendRequestsResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_request);

        try {
            friendRequestsResponse = new FriendRequestsResponse(getIntent().getExtras().getString("response"), "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        LightToolbar lightToolbar = findViewById(R.id.lightToolbar);
        lightToolbar.animateAppear(lightToolbar);

        SliderActivity sliderActivity = new SliderActivity();
        slidrInterface = sliderActivity.attachSlider(this);
        ViewPager viewPager = findViewById(R.id.viewPager);
        requestCountView = findViewById(R.id.requestCountView);
        final FriendsPagerAdapter friendsPagerAdapter = new FriendsPagerAdapter(getSupportFragmentManager(), FriendRequestActivity.this);
        viewPager.setAdapter(friendsPagerAdapter);


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                enableSliding(position != 1);
            }
            @Override
            public void onPageSelected(int position) {
                if(position == 1) {
                    enableSliding(false);
                    ((RequestPage) getSupportFragmentManager().getFragments().get(1)).loadFriendRequests(false);
                }
                else
                {
                    enableSliding(true);
                    ((RequestPage) getSupportFragmentManager().getFragments().get(0)).loadFriendRequests(true);
                }

            }
            @Override
                 public void onPageScrollStateChanged(int state) {
            }
        });

        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void enableSliding(boolean enable){
        if (enable)
            slidrInterface.unlock();
        else
            slidrInterface.lock();
    }

    public class FriendsPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 2;
        private final Context context;
        private RequestPage incomingPage;
        private RequestPage outgoingPage;
        private final int[] tabTitles = new int[] { R.string.incoming, R.string.outgoing };

        public FriendsPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public Fragment getItem(int position) {

            if(position == 0)
            {
                if(incomingPage == null)
                {
                    incomingPage = RequestPage.newInstance(position + 1, context, requestCountView);
                    incomingPage.applyData(friendRequestsResponse, true);
                }
                return incomingPage;
            }
            else if(position == 1)
            {
                if(outgoingPage == null)
                {
                    outgoingPage = RequestPage.newInstance(position + 1, context, requestCountView);
                }
                return outgoingPage;
            }
            else
            {
                return  null;
            }


        }
        @Override
        public CharSequence getPageTitle(int position) {
            return StringsRepository.getOrDefault(tabTitles[position], context);
        }
    }


}

