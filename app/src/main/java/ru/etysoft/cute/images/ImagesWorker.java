package ru.etysoft.cute.images;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import ru.etysoft.cute.utils.Numbers;

public class ImagesWorker {


    public static void setGradient(ImageView imageView, long cid) {
        GradientDrawable gd = new GradientDrawable();
        int[] color = new int[]{Numbers.getColorById(cid, imageView.getContext()), Numbers.getColorById(cid, imageView.getContext())};
        gd.setColors(color);
        gd.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        gd.setShape(GradientDrawable.OVAL);
        gd.setSize(100, 100);
        imageView.setImageDrawable(gd);
    }

    public static void saveBitmapToGallery(Bitmap finalBitmap, Activity activity) {
        String fname = "CuteSaved-" + System.currentTimeMillis() + ".jpg";
        MediaStore.Images.Media.insertImage(activity.getContentResolver(), finalBitmap, fname, "Cute Social Network");
    }

    public static Bitmap getCircleCroppedBitmap(Bitmap bitmap, int height, int width) {
        Bitmap output;
        bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
        if (bitmap.getWidth() > bitmap.getHeight()) {
            output = Bitmap.createBitmap(bitmap.getHeight(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        } else {
            output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        float r = 0;

        if (bitmap.getWidth() > bitmap.getHeight()) {
            r = bitmap.getHeight() / 2;
        } else {
            r = bitmap.getWidth() / 2;
        }

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawCircle(r, r, r, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    public static Bitmap compressBitmap(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()));
    }
}
