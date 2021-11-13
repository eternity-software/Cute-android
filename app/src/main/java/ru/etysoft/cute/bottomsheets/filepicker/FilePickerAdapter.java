package ru.etysoft.cute.bottomsheets.filepicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.Profile;
import ru.etysoft.cute.bottomsheets.conversation.MemberInfo;
import ru.etysoft.cute.bottomsheets.conversation.MembersAdapter;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.components.SmartImageView;
import ru.etysoft.cute.images.WaterfallBalancer;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cuteframework.methods.chat.ChatMember;

public class FilePickerAdapter extends RecyclerView.Adapter<FilePickerAdapter.ViewHolder> {


    private LayoutInflater mInflater;

    private ArrayList<String> images;
    private final int threadCount = 0;
    private WaterfallBalancer waterfallBalancer;
    private Activity context;
    private FilePickerBottomSheet.ItemClickListener itemClickListener;


    // Data is passed into the constructor
    public FilePickerAdapter(Activity context,  FilePickerBottomSheet.ItemClickListener itemClickListener, RecyclerView recyclerView) {
        this.mInflater = LayoutInflater.from(context);
        this.itemClickListener = itemClickListener;
        this.context = context;
        images = getAllShownImagesPath();
        Collections.reverse(images);
        waterfallBalancer = new WaterfallBalancer(context, 10, recyclerView);
    }

    // Inflates the cell layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.file_picker_image, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.smartImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_image));
        return viewHolder;
    }

    // Binds the data to the textview in each cell
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        SmartImageView picturesView = holder.smartImageView;
        picturesView.setImagePath(images.get(position));

        try {
            picturesView.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_image));

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

        public SmartImageView smartImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            SmartImageView picturesView = (SmartImageView) itemView;



          //  picturesView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400));

            smartImageView = picturesView;
            picturesView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            onItemClick(view, getAdapterPosition());
        }
    }

    public ArrayList<String> getImages() {
        return images;
    }

    // Convenience method for getting data at click position
    public String getItem(int id) {
        return images.get(id);
    }

    // Method that executes your code for the action received
    public void onItemClick(View view, int position) {
        Log.i("TAG", "You clicked number " + getItem(position).toString() + ", which is at cell position " + position);
        itemClickListener.onItemClick(position, view);

    }

    private ArrayList<String> getAllShownImagesPath() {
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
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,null, null);
        while (cursor.moveToNext()) {
            String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

            listOfAllImages.add(absolutePathOfImage);
        }
        cursor.close();
        return listOfAllImages;
    }
}
