package ru.etysoft.cute.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.dynamic.ThemeLinearLayout;

public class ThemePreview extends ThemeLinearLayout {
    private View rootView;
    private RelativeLayout backgroundView;
    private ImageView myMessage, chatMessage;
    private TextView themeNameView;

    public ThemePreview(Context context) {
        super(context);
    }

    public ThemePreview(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public ThemePreview(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(attrs);
    }

    public void setThemePreview(String name, int colorBackground, int colorMyMessage, int colorChatMessage) {
        myMessage.setColorFilter(colorMyMessage, PorterDuff.Mode.SRC_ATOP);
        chatMessage.setColorFilter(colorChatMessage, PorterDuff.Mode.SRC_ATOP);
        backgroundView.getBackground().setColorFilter(colorBackground, PorterDuff.Mode.SRC_ATOP);
        themeNameView.setText(name);
    }

    private void initViews(AttributeSet attributeSet) {
        int[] sets = {R.attr.themePreviewBackground, R.attr.themePreviewChatMessageColor, R.attr.themePreviewMyMessageColor,
                R.attr.themePreviewName};
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, sets);

        int background = typedArray.getInteger(0, 1);
        int colorMyMessage = typedArray.getInteger(2, 1);
        int colorChatMessage = typedArray.getInteger(1, 1);
        String name = typedArray.getString(3);


        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.theme_preview, this);
        myMessage = findViewById(R.id.themePreviewMyMessage);
        chatMessage = findViewById(R.id.themePreviewChatMessage);
        themeNameView = findViewById(R.id.themePreviewThemeName);
        backgroundView = findViewById(R.id.themePreviewContainer);

        setThemePreview(name, background, colorMyMessage, colorChatMessage);
    }
}
