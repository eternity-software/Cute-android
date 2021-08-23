package ru.etysoft.cute.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.utils.ImagesWorker;

public class Avatar extends RelativeLayout {

    private TextView acronymView;
    private ImageView generatedPictureView;
    private ImageView pictureView;
    private View rootView;

    public Avatar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initComponent(context);
    }

    public Avatar(Context context) {
        super(context);
    }


    private void initComponent(Context context) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.avatar, this);

        acronymView = (TextView) findViewById(R.id.acronym);
        generatedPictureView = (ImageView) findViewById(R.id.generated);
        pictureView = (ImageView) findViewById(R.id.picture);


        Animation bottomDown = AnimationUtils.loadAnimation(getContext(),
                R.anim.zoom_in);

        rootView.startAnimation(bottomDown);
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    public ImageView getPictureView() {
        return pictureView;
    }

    public void setAcronym(String name) {
        if (name.length() > 0) {
            if (name.length() > 1) {
                acronymView.setText(name.substring(0, 2));
            } else {
                acronymView.setText(name.substring(0, 1));
            }

        }

    }

    public void generateIdPicture(int id) {
        ImagesWorker.setGradient(generatedPictureView, id);
    }
}
