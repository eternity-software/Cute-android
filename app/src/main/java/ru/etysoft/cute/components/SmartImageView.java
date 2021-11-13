package ru.etysoft.cute.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.google.android.material.imageview.ShapeableImageView;

public class SmartImageView extends ShapeableImageView {

    private String imagePath;
    private Bitmap bitmap;

    public SmartImageView(Context context) {
        super(context);
    }
    public SmartImageView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public SmartImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        bitmap = bm;
    }



    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
}
