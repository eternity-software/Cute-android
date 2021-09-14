package ru.etysoft.cute.bottomsheets.filepicker;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.Collections;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ImageEdit.ImageEdit;
import ru.etysoft.cute.components.SmartImageView;
import ru.etysoft.cute.images.WaterfallBalancer;

public class FilePickerBottomSheet extends BottomSheetDialogFragment {
    private View view;
    private ArrayList<String> images;

    public ArrayList<String> getImages() {
        return images;
    }

    private android.widget.AdapterView.OnItemClickListener onItemClickListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.bottom_sheet_filespicker, container, true);
        view = v;

        return v;
    }

    public void setRunnable(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Пустой фон
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);

        getDialog().getWindow()
                .getAttributes().windowAnimations = R.style.DialogAnimation;
        BottomSheetDialog dialog = (BottomSheetDialog) getDialog();


        final FrameLayout bottomSheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);

        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                final CardView cardView = view.findViewById(R.id.appBar);
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {


                    if (cardView.getRadius() != 0) {
                        Thread animation = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                for (int radius = 20; radius >= 0; radius = radius - 1) {
                                    System.out.print(radius);
                                    final int finalRadius = radius;
                                    try {
                                        Thread.sleep(10);


                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                        animation.start();
                    }
                } else {

                    // cardView.setRadius(Numbers.dpToPx(20, getContext()));
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        GridView gallery = bottomSheet.findViewById(R.id.gridView);

        gallery.setAdapter(new ImageAdapter(bottomSheet));

        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1,
                                    int position, long arg3) {
                onItemClickListener.onItemClick(arg0, arg1, position, arg3);
            }
        });
    }

    private class ImageAdapter extends BaseAdapter {

        /**
         * The context.
         */
        private final FrameLayout context;
        private final int threadCount = 0;
        private WaterfallBalancer waterfallBalancer;


        /**
         * Instantiates a new image adapter.
         *
         * @param localContext the local context
         */
        public ImageAdapter(FrameLayout localContext) {
            context = localContext;
            images = getAllShownImagesPath(context);
            Collections.reverse(images);
            waterfallBalancer = new WaterfallBalancer(getActivity(), 10);
        }


        public int getCount() {
            return images.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final SmartImageView picturesView;

            final LayoutInflater layoutInflater = getLayoutInflater();
            picturesView = (SmartImageView) layoutInflater.inflate(R.layout.file_picker_image, null);

            picturesView
                    .setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400));


            picturesView.setImagePath(images.get(position));

            try {
                picturesView.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.icon_image));

                waterfallBalancer.add(picturesView);


            } catch (Exception e) {
                e.printStackTrace();
            }


            return picturesView;
        }


        /**
         * Getting All Images Path.
         *
         * @param activity the activity
         * @return ArrayList with images Path
         */
        private ArrayList<String> getAllShownImagesPath(FrameLayout activity) {
//            Uri uri;
//            Cursor cursor;
//            int column_index_data, column_index_folder_name;
//
//            String absolutePathOfImage = null;
//            uri = MediaStore.Images.Media.E;
//
//
//
//            cursor = activity.getContext().getContentResolver().query(uri, projection, null,
//                    null, null);
//
//            column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
//            column_index_folder_name = cursor
//                    .getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
//            while (cursor.moveToNext()) {
//                absolutePathOfImage = cursor.getString(column_index_data);
//
//                listOfAllImages.add(absolutePathOfImage);
//            }
//
            ArrayList<String> listOfAllImages = new ArrayList<String>();
            String[] projection = {MediaStore.MediaColumns.DATA};
            Cursor cursor = activity.getContext().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,null, null);
            while (cursor.moveToNext()) {
                String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

                listOfAllImages.add(absolutePathOfImage);
            }
            cursor.close();
            return listOfAllImages;
        }
    }

}
