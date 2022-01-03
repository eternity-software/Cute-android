package ru.etysoft.cute.activities.ImageEdit;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.ImageViewCompat;

import java.io.File;
import java.io.IOException;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.DrawingView;
import ru.etysoft.cute.images.ImageRotationFix;
import ru.etysoft.cute.utils.Numbers;

public class ImageEdit extends AppCompatActivity {

    private boolean isEraser = false;
    public final static int RESULT_CODE = 10;

    @SuppressLint("ClickableViewAccessibility")
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


        setupBrushEditor();
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


    @Override
    protected void onStart() {
        super.onStart();


    }

    float startY = 0;
    float downY;
    boolean wasSliding = false;
    int[] colors;
    int currentColor = 0;

    @SuppressLint("ClickableViewAccessibility")
    public void setupBrushEditor() {
        colors = new int[]{getResources().getColor(R.color.paintWhite),
                getResources().getColor(R.color.paintBlack),
                getResources().getColor(R.color.paintRed),
                getResources().getColor(R.color.paintOrange),
                getResources().getColor(R.color.paintYellow),
                getResources().getColor(R.color.paintGreen),
                getResources().getColor(R.color.paintLightBlue),
                getResources().getColor(R.color.paintBlue),
                getResources().getColor(R.color.paintPurple)};
        final LinearLayout brushEditor = findViewById(R.id.brushEditor);
        final ImageView colorPreview = findViewById(R.id.colorPreview);
        final DrawingView drawingView = findViewById(R.id.drawingView);

        drawingView.setColor(colors[0]);

        brushEditor.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        if (startY == 0) {
                            startY = brushEditor.getTop();
                        }
                        downY = event.getRawY();
                        wasSliding = false;

                        break;
                    case MotionEvent.ACTION_MOVE:
                        float offset = downY - event.getRawY();
                        int maxDpOffset = 200;
                        int maxDpSize = 100;
                        int minDpOffset = 2;
                        int maxPxOffset = Numbers.dpToPx(maxDpOffset, ImageEdit.this);
                        int maxPxSize = Numbers.dpToPx(maxDpSize, ImageEdit.this);
                        int minPxOffset = Numbers.dpToPx(minDpOffset, ImageEdit.this);
                        if (offset < maxPxOffset && minPxOffset < offset) {
                            brushEditor.setY(startY - offset);

                            float percentSize = Math.abs(offset) / maxPxSize;
                            float scaleFactor = (float) (1 + percentSize * 0.7);



                            drawingView.setSizePercentage((double) percentSize);
                            ((ImageView) findViewById(R.id.changeBrushButton)).setImageDrawable(getResources().getDrawable(R.drawable.icon_clean));
                            isEraser = false;
                            if (!wasSliding) {

                                wasSliding = true;

                            } else {


                            }
                            brushEditor.setScaleX(scaleFactor);
                            brushEditor.setScaleY(scaleFactor);

                        }


                        break;
                    case MotionEvent.ACTION_UP:
                        brushEditor.animate().y(startY).setDuration(300).setInterpolator(new DecelerateInterpolator(2f)).start();
                        ((ImageView) findViewById(R.id.changeBrushButton)).setImageDrawable(getResources().getDrawable(R.drawable.icon_clean));
                        isEraser = false;
                        if (!wasSliding) {
                            if(currentColor == colors.length - 1)
                            {
                                currentColor = 0;
                            }
                            else
                            {
                                currentColor++;
                            }
                            drawingView.setColor(colors[currentColor]);
                            ImageViewCompat.setImageTintList(colorPreview, ColorStateList.valueOf(colors[currentColor]));


                        }
                        break;


                }
                return true;
            }
        });
    }


    public void changeBrush(View v) {
        final EditText editText = findViewById(R.id.text);
        editText.clearFocus();
        editText.setCursorVisible(false);
        getWindow().getDecorView().clearFocus();


        getWindow().getDecorView().requestFocus();
        final DrawingView drawingView = findViewById(R.id.drawingView);
        if (isEraser) {
            isEraser = false;
            drawingView.setBrush();
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

    public static void open(Uri uri, Activity activity) {
        Intent intent = new Intent(activity, ImageEdit.class);
        intent.putExtra("uri", uri.getPath());
        activity.startActivity(intent);
    }

    public static void openForResult(Uri uri, Activity activity) {
        Intent intent = new Intent(activity, ImageEdit.class);
        intent.putExtra("uri", uri.getPath());
        activity.startActivityForResult(intent, RESULT_CODE);
    }

    float fromX = 0;
    float dX = 0;
    float dY = 0;
    int startWidth = 0;


    public void text(View v) {
        final EditText editText = findViewById(R.id.text);
        editText.setCursorVisible(true);
        editText.requestFocus();
        editText.postDelayed(new Runnable() {
                                 @Override
                                 public void run() {
                                     InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                     keyboard.showSoftInput(editText, 0);
                                 }
                             }
                , 200);
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