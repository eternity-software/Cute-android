package ru.etysoft.cute.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.exceptions.NoSuchValueException;
import ru.etysoft.cute.lang.StringsRepository;

public class ErrorPanel extends RelativeLayout {

    private TextView titleView;
    private TextView textView;
    private ImageView reloadButton;
    private View rootView;


    public ErrorPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        initComponent(context, attrs);
    }

    private void initComponent(Context context, AttributeSet attrs) {
        CharSequence text = "holy sh**t!";
        CharSequence title = "holy sh**t!";
        CharSequence locTextId = "";
        CharSequence locTitleId = "";
        if (attrs != null) {
            int[] sets = {R.attr.text, R.attr.title, R.attr.localizableTextKey, R.attr.localizableTitleKey};
            TypedArray typedArray = context.obtainStyledAttributes(attrs, sets);
            text = typedArray.getText(0);
            title = typedArray.getText(1);
            if (typedArray.length() > 2) {
                locTextId = typedArray.getText(2);
                locTitleId = typedArray.getText(3);
            }
        }
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.error_panel, this);


        titleView = (TextView) findViewById(R.id.error_title);
        textView = (TextView) findViewById(R.id.error_text);
        reloadButton = (ImageView) findViewById(R.id.reload);
        try {
            String customText = StringsRepository.getValue(String.valueOf(locTextId));
            String customTitle = StringsRepository.getValue(String.valueOf(locTitleId));
            titleView.setText(customTitle);
            textView.setText(customText);
        } catch (NoSuchValueException e) {
            titleView.setText(title);
            textView.setText(text);
        }


    }

    public void show() {
        rootView.setVisibility(View.VISIBLE);
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setFillAfter(false);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //and this
        fadeIn.setDuration(100);
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {


            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rootView.startAnimation(fadeIn);
    }

    public void hide(final Runnable runAfter) {

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setFillAfter(false);
        fadeOut.setInterpolator(new DecelerateInterpolator()); //and this
        fadeOut.setDuration(100);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                runAfter.run();
                rootView.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        rootView.startAnimation(fadeOut);
    }


    @Override
    public View getRootView() {
        return rootView;
    }

    public void setReloadAction(final Runnable reloadAction) {
        this.reloadButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadAction.run();
            }
        });
    }

    public void setText(String text) {
        this.textView.setText(text);
    }

    public void setTitle(String title) {
        this.titleView.setText(title);
    }
}
