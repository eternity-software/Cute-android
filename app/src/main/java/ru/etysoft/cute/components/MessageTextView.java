package ru.etysoft.cute.components;

import android.content.Context;
import android.text.Layout;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class MessageTextView extends AppCompatTextView {

    public MessageTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = (int) Math.ceil(getMaxLineWidth(getLayout()));
        int height = getMeasuredHeight();
        setMeasuredDimension(width, height);

        setWidth(width);
    }

    private float getMaxLineWidth(Layout layout) {
        float maximumWidth = 0.0f;
        int lines = layout.getLineCount();
        for (int i = 0; i < lines; i++) {
            maximumWidth = Math.max(layout.getLineWidth(i), maximumWidth);
        }

        return maximumWidth;
    }
}