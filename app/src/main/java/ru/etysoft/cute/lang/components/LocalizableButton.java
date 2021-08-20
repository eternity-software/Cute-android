package ru.etysoft.cute.lang.components;


import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleableRes;
import androidx.appcompat.widget.AppCompatButton;

import org.jetbrains.annotations.NotNull;

import ru.etysoft.cute.R;
import ru.etysoft.cute.exceptions.NoSuchValueException;
import ru.etysoft.cute.lang.StringsRepository;

public class LocalizableButton extends AppCompatButton {
    @StyleableRes
    int localizableKey = 0;
    private CharSequence localId;

    public LocalizableButton(Context context) {
        super(context);
    }

    public LocalizableButton(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs);
    }

    public LocalizableButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initialize(context, attrs);
    }

    private void initialize(Context context, AttributeSet attrs) {
        int[] sets = {R.attr.localizableButtonKey};
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
