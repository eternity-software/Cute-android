package ru.etysoft.cute.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import ru.etysoft.cute.AppSettings;
import ru.etysoft.cute.R;
import ru.etysoft.cute.requests.attachements.ImageFile;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.CustomToast;
import ru.etysoft.cute.utils.ImagesWorker;
import ru.etysoft.cute.utils.Numbers;

public class EditProfile extends AppCompatActivity {

    private int REQUEST_TAKE_PHOTO_FROM_GALLERY = 1;
    private String id;
    private String name;
    private String status;
    private String mCurrentPhotoPath;
    private ImageFile image = null;


    private boolean isImageUpdated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        name = getIntent().getExtras().getString("name");
        status = getIntent().getExtras().getString("status");
        String urlPhoto = (new AppSettings(this)).getString("profilePhoto");
        Slidr.attach(this);

        TextView nameView = findViewById(R.id.name);
        TextView statusView = findViewById(R.id.status);
        ImageView imageView = findViewById(R.id.progileImage);


        if (urlPhoto != null) {
            Picasso.get().load(urlPhoto).placeholder(getResources().getDrawable(R.drawable.circle_gray)).memoryPolicy(MemoryPolicy.NO_CACHE).transform(new CircleTransform()).into(imageView);
        }


        nameView.setText(name);
        statusView.setText(status);
    }

    //TODO: переместить методы

    public static void copyStream(InputStream input, OutputStream output) throws IOException {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }


    private ImageFile createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        ImageFile image = new ImageFile(File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        ).getAbsolutePath());

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public void selectPhoto(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_TAKE_PHOTO_FROM_GALLERY);
    }

    public void apply(View v) {
        HashMap<String, String> params = new HashMap<String, String>();

        TextView nameView = findViewById(R.id.name);
        TextView surnameView = findViewById(R.id.surname);
        TextView statusView = findViewById(R.id.status);
        TextView bioView = findViewById(R.id.bio);

        final Button applyButton = findViewById(R.id.applybtn);
        final ProgressBar wait = findViewById(R.id.loading);
        applyButton.setVisibility(View.INVISIBLE);
        wait.setVisibility(View.VISIBLE);


        final AppSettings appSettings = new AppSettings(this);

        params.put("session", appSettings.getString("session"));

        if (image != null) {

            //params.put("photo", image);
        }


        if (!nameView.getText().equals(name)) {
            params.put("display_name", String.valueOf(nameView.getText()));
        }

        if (!surnameView.getText().equals(name)) {
            params.put("display_surname", String.valueOf(surnameView.getText()));
        }

        if (!bioView.getText().equals(name)) {
            params.put("bio", String.valueOf(bioView.getText()));
        }


        if (!String.valueOf(statusView.getText()).equals(status)) {
            params.put("display_status_text", String.valueOf(statusView.getText()));
        }

        APIRunnable apiRunnable = new APIRunnable() {
            @Override
            public void run() {
                super.run();
                applyButton.setVisibility(View.VISIBLE);
                wait.setVisibility(View.INVISIBLE);

                try {
                    ResponseHandler responseHandler = new ResponseHandler(getResponse());
                    if (responseHandler.isSuccess()) {
                        finish();
                    } else {
                        if (Methods.hasInternet(getApplicationContext())) {
                            CustomToast.show(getResources().getString(R.string.err_unknown), R.drawable.icon_error, EditProfile.this);
                        } else {
                            CustomToast.show(getResources().getString(R.string.err_no_internet), R.drawable.icon_error, EditProfile.this);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };


        Methods.editProfile(params, apiRunnable, this);


    }

    public void back(View v) {
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO_FROM_GALLERY && resultCode == RESULT_OK) {
            try {
                // Creating file
                ImageFile photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.d("ACTIVITYRES", "Error occurred while creating the file");
                }

                InputStream inputStream = getContentResolver().openInputStream(data.getData());
                FileOutputStream fileOutputStream = new FileOutputStream(photoFile);
                // Copying
                copyStream(inputStream, fileOutputStream);
                fileOutputStream.close();
                inputStream.close();
                ImageView imageView = findViewById(R.id.progileImage);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
                image = photoFile;
                int dp = Numbers.dpToPx(150, getApplicationContext());
                imageView.setImageBitmap(ImagesWorker.getCircleCroppedBitmap(bitmap, dp, dp));

            } catch (Exception e) {
                Log.d("ACTIVITYRES", "onActivityResult: " + e.toString());
            }
        }
    }
}