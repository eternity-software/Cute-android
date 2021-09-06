package ru.etysoft.cute.activities;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.utils.ImagesWorker;

public class ImagePreview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        String photo = String.valueOf(getIntent().getExtras().get("url"));
        System.out.println("Previewing " + photo);
        PhotoView photoView = findViewById(R.id.photoView);
        Picasso.get().load(photo).into(photoView);
        photoView.setOnOutsidePhotoTapListener(new OnOutsidePhotoTapListener() {
            @Override
            public void onOutsidePhotoTap(ImageView imageView) {
                onBackPressed();
            }
        });
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

    }

    public void save(View v) {
        PhotoView photoView = findViewById(R.id.photoView);
        Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
        ImagesWorker.saveBitmapToGallery(bitmap, this);
        CuteToast.show(getResources().getString(R.string.saved_to_gallery), R.drawable.icon_success, this);
    }

    public void exit(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_out, R.anim.fade_out);
    }
}