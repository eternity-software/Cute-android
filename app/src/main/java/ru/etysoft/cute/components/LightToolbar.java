package ru.etysoft.cute.components;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.media.Image;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.lang.StringsRepository;
import ru.etysoft.cute.utils.ImagesWorker;

public class LightToolbar extends RelativeLayout {

    private TextView captionView;
    private ImageView backView;
    private View rootView;

    public LightToolbar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initComponent(context, attributeSet);
    }

    public LightToolbar(Context context) {
        super(context);
    }


    private void initComponent(final Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.toolbar, this);
        CharSequence locCaptionId = "";
        CharSequence text = "Test caption";
        if (attrs != null) {
            int[] sets = {R.attr.caption, R.attr.captionLocId};
            TypedArray typedArray = context.obtainStyledAttributes(attrs, sets);
            text = typedArray.getText(0);
            locCaptionId = typedArray.getText(1);
        }
        captionView = (TextView) findViewById(R.id.caption);
        backView = findViewById(R.id.back_button);
        if (context instanceof Activity) {

            backView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((Activity) context).onBackPressed();
                }
            });
        }

        try {
            captionView.setText(StringsRepository.getValue(
                    String.valueOf(locCaptionId)));
        } catch (Exception e)
        {
            captionView.setText(text);
        }





    }

    public void setBackButtonAction(OnClickListener onClick)
    {
        backView.setOnClickListener(onClick);
    }

    public void animateAppear(View rootView)
    {
        Animation slideFromRightAnim = AnimationUtils.loadAnimation(getContext(),
                R.anim.slide_appear);
        rootView.startAnimation(slideFromRightAnim);
    }

    public TextView getCaptionView() {
        return captionView;
    }

    @Override
    public View getRootView() {
        return rootView;
    }
}
