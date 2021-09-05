package ru.etysoft.cute.activities.ImageEdit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import ru.etysoft.cute.R;

public class ImageEdit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        EditText editText = findViewById(R.id.text);

        String uri = getIntent().getExtras().getString("uri");
        final ImageView imageView = findViewById(R.id.mainImageView);
        imageView.setImageURI(Uri.parse(uri));


        editText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    dX = v.getX() - event.getRawX();
                    dY = v.getY() - event.getRawY();


                } else {
                    v.animate()
                            .x(event.getRawX() + dX)
                            .y(event.getRawY() + dY)
                            .setDuration(0)
                            .start();
                }

                return true;
            }
        });
    }

    public void save(View v)
    {
        final ConstraintLayout container = findViewById(R.id.bitmapContainer);
        container.setDrawingCacheEnabled(true);
        Bitmap b = container.getDrawingCache();
        MediaStore.Images.Media.insertImage(getContentResolver(), b, "CUTE_EDITED" + System.currentTimeMillis(), "Cute photo editor");
        finish();
    }

    public static void open(Uri uri, Activity activity)
    {
        Intent intent = new Intent(activity, ImageEdit.class);
        intent.putExtra("uri", uri.getPath());
        activity.startActivity(intent);
    }

    float fromX = 0;
    float dX = 0;
    float dY = 0;
    int startWidth = 0;


    public void text(View v)
    {
        final EditText editText = findViewById(R.id.text);

        editText.requestFocus();
        editText.postDelayed(new Runnable(){
                               @Override public void run(){
                                   InputMethodManager keyboard=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                                   keyboard.showSoftInput(editText,0);
                               }
                           }
                ,200);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        final EditText editText = findViewById(R.id.text);
        editText.clearFocus();
        getWindow().getDecorView().clearFocus();
    }

    public void crop(View v) {
        final ConstraintLayout container = findViewById(R.id.bitmapContainer);
        final EditText editText = findViewById(R.id.text);

        startWidth = container.getLayoutParams().width;
        container.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                getWindow().getDecorView().clearFocus();
                container.requestFocus();
                System.out.println(event.getX());
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    System.out.println("fyck");
                    fromX = event.getX();
                    startWidth = v.getWidth();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                }
                if (fromX == 0)
                {
                    fromX = event.getX();
                }
                int def = (int) event.getX() - (int) fromX;
                int newSize = startWidth + def;
                if(newSize > 200)
                {
                    v.getLayoutParams().width = newSize;
                }
                else
                {
                    System.out.println("oh " + def);
                }


               // fromX = event.getX();
                v.requestLayout();
                return true;
            }
        });


//        CropImageView cropImageView = findViewById(R.id.crop_view);
//        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//        cropImageView.setImageBitmap(bitmap);
//        cropImageView.setScaleType(CropImageView.ScaleType.CENTER_INSIDE);
    }
}