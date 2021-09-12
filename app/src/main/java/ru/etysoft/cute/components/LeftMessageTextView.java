package ru.etysoft.cute.components;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class LeftMessageTextView extends AppCompatTextView {

    // Выгглядит норм, но сжимается
    public LeftMessageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {


        if(getLayout() != null)
        {
            int width = (int) Math.ceil(getMaxLineWidth(getLayout()));
            int height = getMeasuredHeight();
            setMeasuredDimension(width, height);
            super.onMeasure(width, heightMeasureSpec);

        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    private float getMaxLineWidth(Layout layout) {
        float max_width = 0f;
        int lines = layout.getLineCount();
        for (int i = 0; i < lines; i++) {
            if (layout.getLineWidth(i) > max_width) {
                max_width = layout.getLineWidth(i);
            }
        }
        return max_width;
    }
}