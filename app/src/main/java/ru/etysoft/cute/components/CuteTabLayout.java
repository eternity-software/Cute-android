package ru.etysoft.cute.components;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.viewpager.widget.PagerAdapter;

import com.google.android.material.tabs.TabLayout;

public class CuteTabLayout extends TabLayout {
    public CuteTabLayout(Context context) {
        super(context);
    }

    public CuteTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CuteTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setTabsFromPagerAdapter(@NonNull PagerAdapter adapter) {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "font/montserrat.ttf");

        this.removeAllTabs();

        ViewGroup slidingTabStrip = (ViewGroup) getChildAt(0);

        for (int i = 0, count = adapter.getCount(); i < count; i++) {
            Tab tab = this.newTab();
            this.addTab(tab.setText(adapter.getPageTitle(i)));
            AppCompatTextView view = (AppCompatTextView) ((ViewGroup)slidingTabStrip.getChildAt(i)).getChildAt(1);
            view.setTypeface(typeface, Typeface.NORMAL);
        }
    }
}
