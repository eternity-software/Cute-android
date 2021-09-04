package ru.etysoft.cute.images;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.components.SmartImageView;

public class WaterfallBalancer {

    List<WaterfallImageLoader> waterfallImageLoaderList = new ArrayList<>();
    private int lastWaterfallId;

    public WaterfallBalancer(Activity activity, int balancerCount)
    {
        lastWaterfallId = 0;
        for(int i = 0; i < balancerCount; i++)
        {
            waterfallImageLoaderList.add(new WaterfallImageLoader(activity));
        }
    }

    public void add(SmartImageView smartImageView)
    {
        if(lastWaterfallId > waterfallImageLoaderList.size() - 1)
        {
            lastWaterfallId = 0;
        }
        waterfallImageLoaderList.get(lastWaterfallId).add(smartImageView);
        lastWaterfallId++;
    }
}
