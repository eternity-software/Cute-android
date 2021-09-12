package ru.etysoft.cute.activities.imagesend;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.r0adkll.slidr.Slidr;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.ImageEdit.ImageEdit;

public class ImageSendActivity extends AppCompatActivity {

    private String imageUri;

    private String finalImageUri;
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
        ImageView imageView = findViewById(R.id.photoView);
        imageView.setImageURI(Uri.parse(imageUri));

        Slidr.attach(this);

        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
        LinearLayout layout = findViewById(R.id.linearLayout3);
        Animation appearAnimation = AnimationUtils.loadAnimation(this,
                R.anim.appear_from_bottom);
        layout.startAnimation(appearAnimation );
    }

    public void editImageButtonClick(View v)
    {
        ImageEdit.openForResult(Uri.parse(finalImageUri), this);
    }

    public static void open(Activity from, String imageUri, String text)
    {
        Intent intent = new Intent(from, ImageSendActivity.class);
        intent.putExtra("uri", imageUri);
        intent.putExtra("text", text);
        from.startActivityForResult(intent, CODE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        LinearLayout layout = findViewById(R.id.linearLayout3);
        Animation appearAnimation = AnimationUtils.loadAnimation(this,
                R.anim.hide_to_bottom);
        layout.startAnimation(appearAnimation );
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
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