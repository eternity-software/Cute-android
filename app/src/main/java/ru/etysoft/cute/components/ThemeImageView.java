package ru.etysoft.cute.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.StyleableRes;

import ru.etysoft.cute.R;
import ru.etysoft.cute.exceptions.NoSuchValueException;
import ru.etysoft.cute.lang.CustomLanguage;
import ru.etysoft.cute.themes.Theme;

public class ThemeImageView extends androidx.appcompat.widget.AppCompatImageView {

    @StyleableRes
    int themeColor = 0;
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
        int[] sets = { R.attr.themeImageColor};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, sets);
        CharSequence locId = typedArray.getText(0);

        String color = typedArray.getString(0);
        try {
            setColorFilter(Theme.getColor(String.valueOf(color)));
        } catch (NoSuchValueException e) {
            e.printStackTrace();
            setColorFilter(Theme.getColor(getContext(), Theme.getResId(String.valueOf(color), R.color.class)));
        }

        typedArray.recycle();
    }
}
