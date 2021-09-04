package ru.etysoft.cute.images;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.components.SmartImageView;
import ru.etysoft.cute.utils.ImageRotationFix;

public class WaterfallImageLoader {

    private List<SmartImageView> imagesQueue = new ArrayList<>();
    private boolean isRunning;
    private boolean isDataUpdated;
    private Activity activity;

    public WaterfallImageLoader(Activity activity) {
        this.activity = activity;
        isRunning = false;
        isDataUpdated = false;
    }

    public void start() {
        if (!isRunning) {
            isRunning = true;
            Thread worker = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isRunning) {
                        try {

                            WaterfallLogger.log("Starting worker...");
                            List<SmartImageView> localList = (new ArrayList<>());
                            localList.addAll(imagesQueue);
                            for (final SmartImageView imageView : localList) {
                                if (imageView.isShown()) {
                                    WaterfallLogger.log("Loading " + imageView.getImagePath());
                                    try {
                                        final Bitmap bitmap = decodeFile(new File(imageView.getImagePath()));
                                        if (bitmap != null) {
                                            final Bitmap fixedBitmap = ImageRotationFix.handleSamplingAndRotationBitmap(activity, Uri.fromFile(new File(imageView.getImagePath())));
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    if (imageView != null) {
                                                        imageView.setImageBitmap(fixedBitmap);
                                                    }
                                                }
                                            });
                                        }
                                    } catch (final Exception e) {
                                        activity.runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                CuteToast.showError(e.getMessage(), activity);
                                            }
                                        });
                                        e.printStackTrace();
                                    }
                                } else {
                                    activity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.icon_error));
                                        }
                                    });
                                }
                                imagesQueue.remove(imageView);
                            }

                            if (!isDataUpdated) {
                                WaterfallLogger.log("Data update isn't detected");
                                isRunning = false;
                            } else {
                                isDataUpdated = false;
                                WaterfallLogger.log("Updated data detected!");
                            }

                        } catch (ConcurrentModificationException ignored) {
                            ignored.printStackTrace();
                        }
                    }
                    WaterfallLogger.log("Waterfall's worker done!");
                }
            });
            worker.start();
        }
    }

    public void add(SmartImageView imageView) {
        imagesQueue.add(imageView);
        if (isRunning) {
            isDataUpdated = true;
        } else {
            start();
        }
    }

    private Bitmap decodeFile(File f) throws Exception {
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = new FileInputStream(f);
        BitmapFactory.decodeStream(fis, null, o);
        fis.close();

        int scale = 1;

        int MAX_SIZE = 500;

        int IMAGE_MAX_SIZE = Math.max(MAX_SIZE, MAX_SIZE);
        if (o.outHeight > MAX_SIZE || o.outWidth > MAX_SIZE) {
            scale = (int) Math.pow(2, (int) Math.ceil(Math.log(IMAGE_MAX_SIZE /
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        fis = new FileInputStream(f);
        b = BitmapFactory.decodeStream(fis, null, o2);
        fis.close();

        return b;
    }

    private boolean isVisible(final View view) {
        if (view == null) {
            return false;
        }
        if (!view.isShown()) {
            return false;
        }
        return true;
    }

    private static class WaterfallLogger {
        public static void log(String message) {
            System.out.println("WATERFALL LOADER >> " + message);
        }
    }


}
