package ru.etysoft.cute.images;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import ru.etysoft.cute.activities.main.MainActivity;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.components.SmartImageView;

public class WaterfallImageLoader {

    private List<SmartImageView> imagesQueue = new ArrayList<>();
    private boolean isRunning;
    private boolean isDataUpdated;
    private Activity activity;
    private WaterfallCallback waterfallCallback;

    public WaterfallImageLoader(Activity activity) {
        this.activity = activity;
        isRunning = false;
        isDataUpdated = false;
    }

    public void setWaterfallCallback(WaterfallCallback waterfallCallback) {
        this.waterfallCallback = waterfallCallback;
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
                                final boolean[] isCancelled = {false};
                                    WaterfallLogger.log("Loading " + imageView.getImagePath());

                                        try {
                                            final Bitmap bitmap = decodeFile(new File(imageView.getImagePath()));

                                            if (bitmap != null) {
                                                final String oldUri = imageView.getImagePath();
                                                final Bitmap fixedBitmap = ImageRotationFix.handleSamplingAndRotationBitmap(activity, Uri.fromFile(new File(imageView.getImagePath())));
                                                activity.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {


                                                            //  Glide.with(activity).load(imageView.getImagePath()).into(imageView);
                                                            // Old trivial way


                                                            Animation fadeOut = new AlphaAnimation(1, 0);
                                                            fadeOut.setInterpolator(new DecelerateInterpolator()); //add this
                                                            fadeOut.setDuration(200);
                                                            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                                                                @Override
                                                                public void onAnimationStart(Animation animation) {

                                                                }

                                                                @Override
                                                                public void onAnimationEnd(Animation animation) {
                                                                    if(imageView.getImagePath().equals(oldUri)) {
                                                                        imageView.setImageBitmap(fixedBitmap);

                                                                        Animation fadeIn = new AlphaAnimation(0, 1);
                                                                        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                                                                        fadeIn.setDuration(400);

                                                                        imageView.startAnimation(fadeIn);
                                                                    }
                                                                    else
                                                                    {
                                                                        waterfallCallback.onImageReplaced(imageView);
                                                                        if(MainActivity.isDev)
                                                                        {
                                                                            imageView.setBackgroundColor(Color.MAGENTA);
                                                                            imageView.setImageBitmap(null);
                                                                        }
                                                                    }
                                                                }

                                                                @Override
                                                                public void onAnimationRepeat(Animation animation) {

                                                                }
                                                            });

                                                            imageView.startAnimation(fadeOut);
                                                            if (waterfallCallback != null) {
                                                                waterfallCallback.onImageProcessedSuccess(imageView);
                                                            }




                                                    }
                                                });
                                            }
                                        } catch (final Exception e) {
                                            activity.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    CuteToast.showError(e.getMessage(), activity);
                                                    if (MainActivity.isDev) {
                                                        imageView.setBackgroundColor(Color.RED);

                                                    }
                                                    if (waterfallCallback != null) {
                                                        waterfallCallback.onImageProcessedError(imageView);
                                                    }
                                                }
                                            });
                                            e.printStackTrace();

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

    public interface WaterfallCallback
    {
        void onImageProcessedSuccess(SmartImageView smartImageView);
        void onImageProcessedError(SmartImageView smartImageView);
        void onImageReplaced(SmartImageView smartImageView);

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
