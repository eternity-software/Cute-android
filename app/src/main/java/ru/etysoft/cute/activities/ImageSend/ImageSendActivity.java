package ru.etysoft.cute.activities.ImageSend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.Transition;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ImageEdit.ImageEdit;
import ru.etysoft.cute.components.PreviewImageView;
import ru.etysoft.cute.components.SmartImageView;
import ru.etysoft.cute.images.ImageRotationFix;
import ru.etysoft.cute.transition.Transitions;
import ru.etysoft.cute.utils.SliderActivity;

public class ImageSendActivity extends AppCompatActivity {

    private String imageUri;

    private String finalImageUri;
    private static Bitmap imageBuffer;
    private Bitmap imageToSend;
    private PreviewImageView imageView;
    public static final int CODE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_send);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        imageUri = getIntent().getExtras().getString("uri");
        EditText editText = findViewById(R.id.message_box);
        editText.setText(getIntent().getExtras().getString("text"));
        finalImageUri = imageUri;
        Bitmap bitmap = (Bitmap) getIntent().getParcelableExtra("bitmap");




        imageView = findViewById(R.id.photoView);
        imageView.setImageContainer(findViewById(R.id.imageContainer));


        if(imageBuffer != null)
        {
            imageView.setImageBitmap(imageBuffer);
        }

        imageView.setActionsListener(new PreviewImageView.ImageActionsListener() {


            @Override
            public void onSlideDown() {
                imageView.returnToDefaultPos();
            }

            @Override
            public void onSlideUp() {
                onBackPressed();
            }

            @Override
            public void onReturn() {

            }

            @Override
            public void onZoom(float increase) {

            }

            @Override
            public void onSlide(float percent) {

            }

            @Override
            public void onTouch(MotionEvent motionEvent) {

            }

            @Override
            public void onExitZoom() {

            }
        });





        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getSharedElementEnterTransition().setDuration(300);

            LinearLayout layout = findViewById(R.id.linearLayout3);
            layout.setVisibility(View.INVISIBLE);


            getWindow().getSharedElementEnterTransition().setInterpolator(new DecelerateInterpolator(2f));
            getWindow().getSharedElementEnterTransition().addListener(new Transition.TransitionListener() {
                @Override
                public void onTransitionStart(Transition transition) {

                }

                @Override
                public void onTransitionEnd(Transition transition) {

                    showBottomBar();
                    try {
                        final Bitmap fixedBitmap = ImageRotationFix.handleSamplingAndRotationBitmapNoCropping(ImageSendActivity.this, Uri.fromFile(new File(imageUri)));
                        imageView.setImageBitmap(fixedBitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onTransitionCancel(Transition transition) {

                }

                @Override
                public void onTransitionPause(Transition transition) {

                }

                @Override
                public void onTransitionResume(Transition transition) {

                }
            });
        }
        else
        {
            showBottomBar();
        }


        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);

    }


    private void showBottomBar() {
        LinearLayout layout = findViewById(R.id.linearLayout3);
        layout.setVisibility(View.VISIBLE);
        Animation appearAnimation = AnimationUtils.loadAnimation(this,
                R.anim.appear_from_bottom);
        appearAnimation.setFillAfter(true);
        layout.startAnimation(appearAnimation);
    }

    public void editImageButtonClick(View v)
    {
        ImageEdit.openForResult(Uri.parse(finalImageUri), this);
    }

    public static void setImageBuffer(Bitmap imageBuffer) {
        ImageSendActivity.imageBuffer = imageBuffer;
    }

    public static void open(Activity from, String imageUri, String text, SmartImageView smartImageView)
    {
        Intent intent = new Intent(from, ImageSendActivity.class);
        intent.putExtra("uri", imageUri);
        intent.putExtra("text", text);

        setImageBuffer(smartImageView.getBitmap());

        from.startActivityForResult(intent, CODE,
                Transitions.makeOneViewTransition(smartImageView, from, intent, from.getResources().getString(R.string.transition_image_send)));

    }

    @Override
    public void onBackPressed() {
        if(!imageView.isZoomed())
        {
            super.onBackPressed();
            LinearLayout layout = findViewById(R.id.linearLayout3);
            Animation appearAnimation = AnimationUtils.loadAnimation(this,
                    R.anim.hide_to_bottom);
            layout.startAnimation(appearAnimation );
            overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }
        else
        {
            imageView.returnToDefaultPos();
        }

    }

    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (data == null) return;
        if(resultCode == ImageEdit.RESULT_CODE)
        {
            finalImageUri = getRealPathFromURI(this, Uri.parse(data.getStringExtra("uri")));
            ImageView imageView = findViewById(R.id.photoView);
            imageView.setImageURI(Uri.parse(finalImageUri));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void sendButtonClick(View v)
    {
        EditText editText = findViewById(R.id.message_box);

        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        Intent intent = new Intent();
        intent.putExtra("uri", finalImageUri);
        intent.putExtra("text", editText.getText().toString());
        setResult(CODE, intent);
        finish();
    }
}