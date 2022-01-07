package ru.etysoft.cute.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.StyleableRes;

import ru.etysoft.cute.R;
import ru.etysoft.cute.exceptions.NoSuchValueException;
import ru.etysoft.cute.themes.Theme;

public class ThemeLinearLayout extends LinearLayout {
    @StyleableRes
    int themeColor = 0;
    private CharSequence localId;

    public ThemeLinearLayout(Context context) {
        super(context);
    }

    public ThemeLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public ThemeLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        int[] sets = { R.attr.themeBackgroundColor};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, sets);


        String color = typedArray.getString(0);
        try {
            getBackground().setColorFilter(Theme.getColor(String.valueOf(color)), PorterDuff.Mode.SRC);
        } catch (NoSuchValueException e) {
            e.printStackTrace();
            getBackground().setColorFilter(Theme.getColor(getContext(), Theme.getResId(String.valueOf(color), R.color.class)), PorterDuff.Mode.SRC);
        }

        typedArray.recycle();
    }
}
