package ru.etysoft.cute.components;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.images.ImagesWorker;
import ru.etysoft.cute.utils.Numbers;

public class Avatar extends RelativeLayout implements Parcelable {

    private TextView acronymView;
    private ImageView generatedPictureView;
    private ImageView pictureView;
    private ImageView onlineView;
    private View rootView;
    private int size;

    public Avatar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initComponent(context);
    }

    public Avatar(Context context) {
        super(context);
        initComponent(context);
    }


    private void initComponent(Context context) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        rootView = inflater.inflate(R.layout.avatar, this);

        acronymView = findViewById(R.id.acronym);
        generatedPictureView = findViewById(R.id.generated);
        pictureView = findViewById(R.id.picture);
        onlineView = findViewById(R.id.onlineView);
        onlineView.setVisibility(View.INVISIBLE);

    }

    public void setOnline(boolean isOnline)
    {
        onlineView.setVisibility(VISIBLE);
        if(isOnline)
        {
            onlineView.setBackground(getResources().getDrawable(R.drawable.circle_online));
        }
        else
        {
            onlineView.setBackground(getResources().getDrawable(R.drawable.circle_offline));
        }
    }

    @Override
    public View getRootView() {
        return rootView;
    }

    public ImageView getPictureView() {
        return pictureView;
    }

    public void setAcronym(String name, int size) {
        if (name.length() > 0) {
            if (name.length() > 1) {
                String finalName = name.substring(0, 1).toUpperCase() + name.substring(1, 2).toLowerCase();
                acronymView.setText(finalName);
            } else {
                acronymView.setText(name.substring(0, 1));
            }

        }
        setSize(size);
    }

    private int coverPixelToDP (int dps) {
        final float scale = this.getResources().getDisplayMetrics().density;
        return (int) (dps * scale);
    }

    public void showAnimate()
    {
        Animation bottomDown = AnimationUtils.loadAnimation(getContext(),
                R.anim.zoom_in);

        bottomDown.setInterpolator(new DecelerateInterpolator(1f));

        rootView.startAnimation(bottomDown);
    }

    public void setSize(int size)
    {
        if(size == Size.SMALL)
        {
            onlineView.getLayoutParams().width = Numbers.dpToPx(14, getContext());
            onlineView.getLayoutParams().height = Numbers.dpToPx(14, getContext());
            acronymView.setTextSize(coverPixelToDP(6));
        }
        else if(size == Size.MEDIUM)
        {
            onlineView.getLayoutParams().width = Numbers.dpToPx(18, getContext());
            onlineView.getLayoutParams().height = Numbers.dpToPx(18, getContext());
            acronymView.setTextSize(coverPixelToDP(8));
        }
        else if(size == Size.LARGE)
        {
            acronymView.setTextSize(coverPixelToDP(12));
        }
        this.size = size;
    }

    public int getSize() {
        return size;
    }

    public TextView getAcronymView() {
        return acronymView;
    }

    public void generateIdPicture(long id) {
        ImagesWorker.setGradient(generatedPictureView, id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    public static class Size
    {
        public static final int SMALL = 1;
        public static final int MEDIUM = 2;
        public static final int LARGE = 3;

    }
}
