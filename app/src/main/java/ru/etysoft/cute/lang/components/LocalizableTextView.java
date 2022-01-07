package ru.etysoft.cute.lang.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.StyleableRes;
import androidx.appcompat.widget.AppCompatTextView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.exceptions.NoSuchValueException;
import ru.etysoft.cute.lang.CustomLanguage;
import ru.etysoft.cute.lang.StringsRepository;
import ru.etysoft.cute.themes.Theme;


public class LocalizableTextView extends AppCompatTextView {

    @StyleableRes
    int localizableKey = 0;
    @StyleableRes
    int themeColor = 0;
    private CharSequence localId;

    public LocalizableTextView(Context context) {
        super(context);
    }

    public LocalizableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public LocalizableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        int[] sets = {R.attr.localizableKey, R.attr.themeColor, R.attr.themeColorLink};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, sets);
        CharSequence locId = typedArray.getText(0);
        CharSequence textColor = typedArray.getString(1);
        CharSequence textLinkColor = typedArray.getString(2);
        localId = locId;
        try {
            setTextColor(Theme.getColor(String.valueOf(textColor)));
        } catch (NoSuchValueException e) {
            e.printStackTrace();
            setTextColor(Theme.getColor(getContext(), Theme.getResId(String.valueOf(textColor), R.color.class)));
        }

        try {
            setLinkTextColor(Theme.getColor(String.valueOf(textLinkColor)));
        } catch (NoSuchValueException e) {
            e.printStackTrace();
            setLinkTextColor(Theme.getColor(getContext(), Theme.getResId(String.valueOf(textLinkColor), R.color.class)));
        }
        try {
            setText(CustomLanguage.getStringsRepository().getValue(String.valueOf(locId)));
        } catch (NoSuchValueException ignored) {
        }
        typedArray.recycle();
    }


}