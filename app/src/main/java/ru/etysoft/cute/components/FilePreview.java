package ru.etysoft.cute.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ru.etysoft.cute.R;
import ru.etysoft.cute.bottomsheets.filepicker.FileInfo;

public class FilePreview extends RelativeLayout {

    private View rootView;
    private FileInfo fileInfo;
    private FileParingImageView fileParingImageView;
    private RelativeLayout videoInfoView;
    private TextView durationView;
    private boolean isInitialized = false;

    public FilePreview(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public void initComponent() {

        if(!isInitialized) {

            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rootView = inflater.inflate(R.layout.file_preview, this);
            fileParingImageView = findViewById(R.id.fileImageView);
            videoInfoView = findViewById(R.id.videoInfo);
            durationView = findViewById(R.id.durationView);
            isInitialized = true;
        }


    }

    public FileParingImageView getFileParingImageView() {
        initComponent();
        return fileParingImageView;
    }

    public void setFileInfo(FileInfo fileInfo) {
        this.fileInfo = fileInfo;
        initComponent();
        fileParingImageView.setFileInfo(fileInfo);
        if(fileInfo.isVideo())
        {
            videoInfoView.setVisibility(VISIBLE);
            durationView.setText(fileInfo.getFormattedVideoDuration(getContext()));
        }
        else
        {
            videoInfoView.setVisibility(INVISIBLE);
        }
    }



    public FileInfo getFileInfo() {
        return fileInfo;
    }
}
