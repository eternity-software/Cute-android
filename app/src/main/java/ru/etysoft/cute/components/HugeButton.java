package ru.etysoft.cute.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.exceptions.NoSuchValueException;
import ru.etysoft.cute.lang.CustomLanguage;
import ru.etysoft.cute.themes.Theme;

public class HugeButton extends LinearLayout {

    private View rootView;
    private boolean wasInitialized = false;

    public HugeButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        initComponent(attrs);
    }


    public void initComponent(AttributeSet attrs) {
        if (!wasInitialized) {
            int[] sets = {R.attr.hugeButtonText};
            TypedArray typedArray = getContext().obtainStyledAttributes(attrs, sets);
            String text = typedArray.getString(0);





            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rootView = inflater.inflate(R.layout.huge_button_element, this);
            TextView textView = findViewById(R.id.hugeButtonText);
            ImageView imageView = findViewById(R.id.hugeButtonIcon);



            try {
                textView.setText(CustomLanguage.getStringsRepository().getValue(String.valueOf(text)));
            } catch (NoSuchValueException ignored) {

                textView.setText(getResources().getString(Theme.getResId(text, R.string.class)));
            }

            TypedArray a = getContext().obtainStyledAttributes(attrs, new int[]{R.attr.hugeButtonIcon});
            Drawable drawable = a.getDrawable(0);
            if (drawable != null)
                imageView.setImageDrawable(drawable);
            wasInitialized = true;
            a.recycle();
            typedArray.recycle();
        }
    }
}
