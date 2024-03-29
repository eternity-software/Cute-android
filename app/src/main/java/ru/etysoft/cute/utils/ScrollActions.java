package ru.etysoft.cute.utils;

import android.widget.ListView;

public class ScrollActions {
    public static boolean isScrollable(ListView listView) {
        if (listView.getAdapter().getCount() == 0) return false;
        int pos = listView.getLastVisiblePosition();
        if (pos <= 0) return false;
        return listView.getChildAt(pos).getBottom() > listView.getHeight();
    }
}
