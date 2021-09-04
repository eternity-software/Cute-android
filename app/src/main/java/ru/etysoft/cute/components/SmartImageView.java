package ru.etysoft.cute.components;

import android.content.Context;
import android.provider.ContactsContract;
import android.widget.ImageView;

public class SmartImageView extends androidx.appcompat.widget.AppCompatImageView {

    private String imagePath;

    public SmartImageView(Context context) {
        super(context);
    }

    public void setImagePath(String imagePath)
    {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return imagePath;
    }
}
