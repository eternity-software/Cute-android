package ru.etysoft.cute.images;

import android.app.Activity;
import android.graphics.Color;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.activities.main.MainActivity;
import ru.etysoft.cute.components.FileParingImageView;
import ru.etysoft.cute.components.FilePreview;

public class WaterfallBalancer {

    List<WaterfallImageLoader> waterfallImageLoaderList = new ArrayList<>();
    ArrayList<FilePreview> imagesInTask = new ArrayList<>();
    ArrayList<FilePreview> cancelledList = new ArrayList<>();
    private int lastWaterfallId;
    private int activeWaterfalls = 0;
    private TextView debugText;
    private BalancerCallback balancerCallback;


    public static interface BalancerCallback
    {
        void onActiveWaterfallsCountChange(int count);
    }

    public void setBalancerCallback(BalancerCallback balancerCallback) {
        this.balancerCallback = balancerCallback;
    }

    public WaterfallBalancer(Activity activity, int balancerCount, final RecyclerView recyclerView)
    {
        lastWaterfallId = 0;
        for(int i = 0; i < balancerCount; i++)
        {
            WaterfallImageLoader waterfallImageLoader = new WaterfallImageLoader(activity);
            waterfallImageLoader.setWaterfallCallback(new WaterfallImageLoader.WaterfallCallback() {
                @Override
                public void onImageProcessedSuccess(FilePreview fileParingImageView) {
                    imagesInTask.remove(fileParingImageView);
                    if(MainActivity.isDev)
                    {
                        fileParingImageView.setBackgroundColor(Color.GREEN);
                    }

                }

                @Override
                public void onImageProcessedError(FilePreview fileParingImageView) {
                    imagesInTask.remove(fileParingImageView);
                    if(MainActivity.isDev)
                    {
                        fileParingImageView.setBackgroundColor(Color.RED);
                    }
                }

                @Override
                public void onImageReplaced(FilePreview fileParingImageView) {

                    imagesInTask.remove(fileParingImageView);
                    add(fileParingImageView);
                }

                @Override
                public void onFinishedAllTasks() {
                    activeWaterfalls -= 1;
                    if(activeWaterfalls == 0) clearCancelled();
                    if(balancerCallback != null)
                    {
                        balancerCallback.onActiveWaterfallsCountChange(activeWaterfalls);
                    }
                }

                @Override
                public void onStarted() {
                    activeWaterfalls++;

                    if(balancerCallback != null)
                    {
                        balancerCallback.onActiveWaterfallsCountChange(activeWaterfalls);
                    }
                }


            });
            waterfallImageLoaderList.add(waterfallImageLoader);
        }
    }

    public void clearCancelled()
    {
        for(FilePreview cancelled : cancelledList)
        {
           // imagesInTask.remove(cancelled);
        }
    }

    public void add(FilePreview fileParingImageView) {


        if (imagesInTask.contains(fileParingImageView)) {
            cancelledList.add(fileParingImageView);
            if (MainActivity.isDev) {
                fileParingImageView.setBackgroundColor(Color.DKGRAY);
            }
        }
        else
        {
            if (MainActivity.isDev) {
                fileParingImageView.setBackgroundColor(Color.YELLOW);
            }
            if (lastWaterfallId > waterfallImageLoaderList.size() - 1) {
                lastWaterfallId = 0;
            }
            waterfallImageLoaderList.get(lastWaterfallId).add(fileParingImageView);
            imagesInTask.add(fileParingImageView);
            lastWaterfallId++;

        }




    }
}
