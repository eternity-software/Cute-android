package ru.etysoft.cute.images;

import android.app.Activity;
import android.graphics.Color;
import android.os.Parcelable;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.etysoft.cute.activities.main.MainActivity;
import ru.etysoft.cute.components.SmartImageView;

public class WaterfallBalancer {

    List<WaterfallImageLoader> waterfallImageLoaderList = new ArrayList<>();
    ArrayList<SmartImageView> imagesInTask = new ArrayList<>();
    ArrayList<SmartImageView> cancelledList = new ArrayList<>();
    private int lastWaterfallId;

    public WaterfallBalancer(Activity activity, int balancerCount, final RecyclerView recyclerView)
    {
        lastWaterfallId = 0;
        for(int i = 0; i < balancerCount; i++)
        {
            WaterfallImageLoader waterfallImageLoader = new WaterfallImageLoader(activity);
            waterfallImageLoader.setWaterfallCallback(new WaterfallImageLoader.WaterfallCallback() {
                @Override
                public void onImageProcessedSuccess(SmartImageView smartImageView) {
                    imagesInTask.remove(smartImageView);
                    if(MainActivity.isDev)
                    {
                        smartImageView.setBackgroundColor(Color.GREEN);
                    }
                }

                @Override
                public void onImageProcessedError(SmartImageView smartImageView) {
                    imagesInTask.remove(smartImageView);
                    if(MainActivity.isDev)
                    {
                        smartImageView.setBackgroundColor(Color.RED);
                    }
                }

                @Override
                public void onImageReplaced(SmartImageView smartImageView) {
                    add(smartImageView);
                }


            });
            waterfallImageLoaderList.add(waterfallImageLoader);
        }
    }

    public void add(SmartImageView smartImageView) {


        if (imagesInTask.contains(smartImageView)) {
            cancelledList.add(smartImageView);
        }
        else
        {
            if (MainActivity.isDev) {
                smartImageView.setBackgroundColor(Color.YELLOW);
            }
            if (lastWaterfallId > waterfallImageLoaderList.size() - 1) {
                lastWaterfallId = 0;
            }
            waterfallImageLoaderList.get(lastWaterfallId).add(smartImageView);
            imagesInTask.add(smartImageView);
            lastWaterfallId++;

        }




    }
}
