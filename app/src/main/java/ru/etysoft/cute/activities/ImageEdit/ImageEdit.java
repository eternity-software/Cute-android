package ru.etysoft.cute.activities.ImageEdit;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.File;
import java.io.IOException;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.DrawingView;
import ru.etysoft.cute.images.ImageRotationFix;

public class ImageEdit extends AppCompatActivity {

    private boolean isEraser = false;
    public final static int RESULT_CODE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        final EditText editText = findViewById(R.id.text);

        String uri = getIntent().getExtras().getString("uri");
        final ImageView imageView = findViewById(R.id.mainImageView);
        try {
            imageView.setImageBitmap(ImageRotationFix.handleSamplingAndRotationBitmapNoCropping(this, Uri.fromFile(new File(uri))));
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }


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



    public void brush(View v) {
        final EditText editText = findViewById(R.id.text);
        editText.clearFocus();
        editText.setCursorVisible(false);
        getWindow().getDecorView().clearFocus();


        getWindow().getDecorView().requestFocus();
        final DrawingView drawingView = findViewById(R.id.draw_view);
        if (isEraser) {
            isEraser = false;
            drawingView.setPaint();
            ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.icon_clean));
        } else {
            isEraser = true;
            drawingView.setEraser();
            ((ImageView) v).setImageDrawable(getResources().getDrawable(R.drawable.icon_brush));

        }
    }

    public void save(View v) {
        getWindow().getDecorView().clearFocus();
        getWindow().getDecorView().requestFocus();
        final EditText editText = findViewById(R.id.text);
        editText.setCursorVisible(false);
        final ConstraintLayout container = findViewById(R.id.bitmapContainer);
        container.setDrawingCacheEnabled(true);
        Bitmap b = container.getDrawingCache();
        String result = MediaStore.Images.Media.insertImage(getContentResolver(), b, "CUTE_EDITED" + System.currentTimeMillis(), "Cute photo editor");

        Intent intent = new Intent();
        intent.putExtra("uri", result);
        setResult(RESULT_CODE, intent);

        finish();
    }

    public static void open(Uri uri, Activity activity)
    {
        Intent intent = new Intent(activity, ImageEdit.class);
        intent.putExtra("uri", uri.getPath());
        activity.startActivity(intent);
    }

    public static void openForResult(Uri uri, Activity activity)
    {
        Intent intent = new Intent(activity, ImageEdit.class);
        intent.putExtra("uri", uri.getPath());
        activity.startActivityForResult(intent, RESULT_CODE);
    }

    float fromX = 0;
    float dX = 0;
    float dY = 0;
    int startWidth = 0;


    public void text(View v)
    {
        final EditText editText = findViewById(R.id.text);
        editText.setCursorVisible(true);
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
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
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
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    fromX = event.getX();
                    startWidth = v.getWidth();
                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                }
                if (fromX == 0) {
                    fromX = event.getX();
                }
                int def = (int) event.getX() - (int) fromX;
                int newSize = startWidth + def;
                if (newSize > 200) {
                    v.getLayoutParams().width = newSize;
                } else {


                    // fromX = event.getX();
                    v.requestLayout();

                }
                return true;
            }

        });


//        CropImageView cropImageView = findViewById(R.id.crop_view);
//        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
//        cropImageView.setImageBitmap(bitmap);
//        cropImageView.setScaleType(CropImageView.ScaleType.CENTER_INSIDE);
    }
}