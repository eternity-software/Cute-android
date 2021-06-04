package ru.etysoft.cute.utils;

import android.view.View;
import android.widget.PopupWindow;

import java.util.ArrayList;
import java.util.HashMap;

public class TooltipScript {

    private HashMap<View, Tooltip> script = new HashMap<>();
    private int lastIndex = 0;

    public TooltipScript(HashMap<View, Tooltip> script) {
        this.script = script;
    }

    public TooltipScript() {

    }

    public void addTooltip(Tooltip newTooltip, View anchor) {
        script.put(anchor, newTooltip);
    }

    public void start() {
        try {
            nextTooltip();
        } catch (Exception e) {

        }
    }

    public void nextTooltip() {
        ArrayList<View> keys = new ArrayList<>(script.keySet());
        if (lastIndex <= keys.size() - 1) {
            View anchor = keys.get(lastIndex);
            Tooltip tooltip = script.get(anchor);
            tooltip.showAsDropDown(anchor, 0, Numbers.dpToPx(8, tooltip.getContentView().getContext()));
            tooltip.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {

                    nextTooltip();
                }
            });
            lastIndex++;
        }
    }
}
