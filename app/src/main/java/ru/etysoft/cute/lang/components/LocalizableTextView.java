package ru.etysoft.cute.lang.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.StyleableRes;
import androidx.appcompat.widget.AppCompatTextView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.exceptions.NoSuchValueException;
import ru.etysoft.cute.lang.StringsRepository;


public class LocalizableTextView extends AppCompatTextView {

    @StyleableRes
    int localizableKey = 0;
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
        int[] sets = {R.attr.localizableKey};
        TypedArray typedArray = context.obtainStyledAttributes(attrs, sets);
        CharSequence locId = typedArray.getText(localizableKey);
        localId = locId;
        try {
            setText(StringsRepository.getValue(String.valueOf(locId)));
        } catch (NoSuchValueException ignored) {
        }
        typedArray.recycle();
    }


}