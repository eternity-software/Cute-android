package ru.etysoft.cute.bottomsheets.filepicker;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.FileParingImageView;
import ru.etysoft.cute.components.FilePreview;
import ru.etysoft.cute.images.WaterfallBalancer;

public class FilePickerAdapter extends RecyclerView.Adapter<FilePickerAdapter.ViewHolder> {


    private LayoutInflater mInflater;

    private ArrayList<FileInfo> images;
    private final int threadCount = 0;
    private WaterfallBalancer waterfallBalancer;
    private Activity context;
    private FilePickerBottomSheet.ItemClickListener itemClickListener;

    public void setBalancerCallback(WaterfallBalancer.BalancerCallback balancerCallback)
    {
        waterfallBalancer.setBalancerCallback(balancerCallback);
    }

    // Data is passed into the constructor
    public FilePickerAdapter(final Activity context, FilePickerBottomSheet.ItemClickListener itemClickListener, RecyclerView recyclerView) {
        this.mInflater = LayoutInflater.from(context);
        this.itemClickListener = itemClickListener;
        this.context = context;
        images = getAllShownImagesPath();
     //   Collections.reverse(images);
        waterfallBalancer = new WaterfallBalancer(context, 10, recyclerView);

    }

    // Inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.file_picker_image, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.fileParingImageView.getFileParingImageView().setImageDrawable(context.getResources().getDrawable(R.drawable.icon_image));
        return viewHolder;
    }

    // Binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FilePreview picturesView = holder.fileParingImageView;
        picturesView.setFileInfo(images.get(position));
        System.out.println(images.get(position).getMimeType());

        try {
            picturesView.getFileParingImageView().setImageDrawable(context.getResources().getDrawable(R.drawable.icon_image));

            waterfallBalancer.add(picturesView);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Total number of cells
    @Override
    public int getItemCount() {
        return images.size();
    }

    // Stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public FilePreview fileParingImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            FilePreview picturesView = (FilePreview) itemView;



          //  picturesView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400));

            fileParingImageView = picturesView;
            picturesView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            onItemClick(view, getAdapterPosition());
        }
    }

    public ArrayList<FileInfo> getImages() {
        return images;
    }

    // Convenience method for getting data at click position
    public FileInfo getItem(int id) {
        return images.get(id);
    }

    // Method that executes your code for the action received
    public void onItemClick(View view, int position) {
        Log.i("TAG", "You clicked number " + getItem(position).toString() + ", which is at cell position " + position);
        itemClickListener.onItemClick(position, view);

    }

    private ArrayList<FileInfo> getAllShownImagesPath() {
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
//                listOfAllMedia.add(absolutePathOfImage);
//            }
//
//        ArrayList<String> listOfAllMedia = new ArrayList<String>();
//        String[] projection = {MediaStore.MediaColumns.DATA};
//        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,null, null);
//
//        while (cursor.moveToNext()) {
//            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
//
//            listOfAllMedia.add(absolutePathOfImage);
//        }
//        cursor.close();
        ArrayList<FileInfo> listOfAllMedia = new ArrayList<FileInfo>();
        String[] projection = {
                MediaStore.Files.FileColumns._ID,
                MediaStore.Files.FileColumns.DATA,
                MediaStore.Files.FileColumns.DATE_ADDED,
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.TITLE
        };

// Return only video and image metadata.
        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
                + " OR "
                + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO;

        Uri queryUri = MediaStore.Files.getContentUri("external");

        CursorLoader cursorLoader = new CursorLoader(
                context,
                queryUri,
                projection,
                selection,
                null, // Selection args (none).
                MediaStore.Files.FileColumns.DATE_ADDED + " DESC" // Sort order.
        );

        Cursor cursor = cursorLoader.loadInBackground();
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
            String mimeType = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.MIME_TYPE));
            String title = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.TITLE));

            if(mimeType.startsWith("image") || mimeType.startsWith("video"))
            {
                listOfAllMedia.add(new FileInfo(title, absolutePathOfImage, mimeType));
            }
        }
        cursor.close();
        return listOfAllMedia;
    }
}
