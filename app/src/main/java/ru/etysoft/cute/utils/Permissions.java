package ru.etysoft.cute.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.MainActivity;
import ru.etysoft.cute.bottomsheets.FloatingBottomSheet;

public class Permissions {

    public static void checkAvailble(final Activity activity) {
        if (ContextCompat.checkSelfPermission(
                activity,
                "android.permission.WRITE_EXTERNAL_STORAGE")
                == PackageManager.PERMISSION_DENIED) {
            final FloatingBottomSheet floatingBottomSheet = new FloatingBottomSheet();

            View.OnClickListener runnable = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat
                            .requestPermissions(
                                    activity,
                                    new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},
                                    1);

                    floatingBottomSheet.dismiss();
                }
            };


            floatingBottomSheet.setContent(activity.getResources().getDrawable(R.drawable.icon_open),
                    activity.getResources().getString(R.string.perms_title),
                    activity.getResources().getString(R.string.perms_text),
                    activity.getResources().getString(R.string.perms_btn),
                    null,
                    runnable,
                    null);
            floatingBottomSheet.setCancelable(false);
            floatingBottomSheet.show(((MainActivity) activity).getSupportFragmentManager(), "tag");


        } else {

        }
    }

}
