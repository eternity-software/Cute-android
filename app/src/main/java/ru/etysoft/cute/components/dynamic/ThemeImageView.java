package ru.etysoft.cute.components.dynamic;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleableRes;

import ru.etysoft.cute.R;
import ru.etysoft.cute.exceptions.NoSuchValueException;
import ru.etysoft.cute.lang.CustomLanguage;
import ru.etysoft.cute.themes.Theme;

public class ThemeImageView extends androidx.appcompat.widget.AppCompatImageView {


    private CharSequence localId;

    public ThemeImageView(Context context) {
        super(context);
    }

    public ThemeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public ThemeImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {




        try {
            int[] sets = { R.attr.themeBackgroundTint, R.attr.themeImageColor};
            TypedArray typedArray = context.obtainStyledAttributes(attrs, sets);
            String backgroundTint = String.valueOf(typedArray.getText(0));
            String color = String.valueOf(typedArray.getText(1));

            try {
                setColorFilter(Theme.getColor(String.valueOf(color)));
            } catch (NoSuchValueException e) {
                setColorFilter(Theme.getColor(getContext(), Theme.getResId(String.valueOf(color), R.color.class)));
            }

            try {
                System.out.println("Getting background tint " + backgroundTint + " " + color);
                getBackground().setColorFilter(Theme.getColor(String.valueOf(backgroundTint)), PorterDuff.Mode.SRC_ATOP);
            } catch (NoSuchValueException e) {

                if (getBackground() != null)
                    getBackground().setColorFilter(Theme.getColor(getContext(), Theme.getResId(String.valueOf(backgroundTint), R.color.class)), PorterDuff.Mode.SRC_ATOP);
            }
            typedArray.recycle();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


    }
}
