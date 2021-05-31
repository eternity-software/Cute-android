package ru.etysoft.cute.utils;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupWindow;
import android.widget.TextView;

import ru.etysoft.cute.R;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Tooltip extends PopupWindow {

    private final Context context;
    private final TextView tooltipText;
    private boolean canDismiss = false;
    private boolean canAnimate = true;
    private Drawable oldDrawable;
    private View anchor;

    public Tooltip(Context context) {
        super(context);
        this.context = context;
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.tooltip, null);
        tooltipText = popupView.findViewById(R.id.tooltiptext);
        this.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        setClippingEnabled(true);
        setFocusable(true);
        setContentView(popupView);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        oldDrawable = anchor.getBackground();
        this.anchor = anchor;
        anchor.setBackgroundColor(anchor.getContext().getResources().getColor(R.color.colorAccent));
        super.showAsDropDown(anchor, xoff, yoff);
    }

    @Override
    public void showAsDropDown(View anchor) {
        oldDrawable = anchor.getBackground();
        this.anchor = anchor;
        anchor.setBackgroundColor(anchor.getContext().getResources().getColor(R.color.colorAccent));
        super.showAsDropDown(anchor);
    }

    public void setText(String tooltipText) {
        this.tooltipText.setText(tooltipText);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        Rect location = locateView(parent);
        Log.d("Fuck", "par" + parent.getY());
        super.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, 0, 0);
        int ypos = 0;
        if (parent.getY() < getContentView().getHeight()) {
            ypos = (int) parent.getY() + parent.getHeight() * 2;
        } else {
            ypos = (int) parent.getY() - parent.getHeight() - getContentView().getHeight();
        }
        Log.d("Fuck", "ypos" + ypos);

        Animation aniFade2 = AnimationUtils.loadAnimation(context, R.anim.zoom_in);
        getContentView().startAnimation(aniFade2);
        super.showAtLocation(parent, Gravity.TOP | Gravity.LEFT, 0, ypos);
    }

    public Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1] - getContentView().getHeight();
        location.right = location.left + v.getWidth();
        location.bottom = location.top + getContentView().getHeight();
        return location;
    }

    @Override
    public void dismiss() {
        if (canDismiss) {
            super.dismiss();
        }

        if (canAnimate) {
            Animation aniFade2 = AnimationUtils.loadAnimation(context, R.anim.zoom_out);
            getContentView().startAnimation(aniFade2);
            aniFade2.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    canAnimate = false;
                    anchor.setBackground(oldDrawable);
                    anchor.setBackgroundColor(anchor.getContext().getResources().getColor(R.color.colorTransparent));
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    canDismiss = true;

                    dismiss();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }
}
