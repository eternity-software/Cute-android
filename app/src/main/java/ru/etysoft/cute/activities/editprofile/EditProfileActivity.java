package ru.etysoft.cute.activities.editprofile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.r0adkll.slidr.Slidr;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.components.LightToolbar;
import ru.etysoft.cute.data.CacheUtils;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.ChangeAvatar.ChangeAvatarRequest;
import ru.etysoft.cuteframework.methods.account.ChangeAvatar.ChangeAvatarResponse;
import ru.etysoft.cuteframework.methods.account.EditDisplayName.EditRequest;
import ru.etysoft.cuteframework.methods.account.EditDisplayName.EditResponse;
import ru.etysoft.cuteframework.methods.media.UploadImageRequest;
import ru.etysoft.cuteframework.methods.media.UploadImageResponse;
import ru.etysoft.cuteframework.requests.attachements.ImageFile;

public class EditProfileActivity extends AppCompatActivity {

    private int REQUEST_TAKE_PHOTO_FROM_GALLERY = 1;
    private String id;
    private String name;
    private String login;
    private LightToolbar toolbar;
    private String mCurrentPhotoPath;
    private ImageFile image = null;
    private TextView nameView;
    private TextView statusView;
    private TextView bioView;


    private boolean isImageUpdated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        name = getIntent().getExtras().getString("name");
        login = getIntent().getExtras().getString("login");
        String urlPhoto = (CacheUtils.getInstance()).getString("profilePhoto", this);


        nameView = findViewById(R.id.editTextAccountName);
        statusView = findViewById(R.id.statusView);
        bioView = findViewById(R.id.bioView);


        toolbar = findViewById(R.id.toolbar);
        toolbar.animateAppear();
        TextView loginView = findViewById(R.id.editTextAccountLogin);
//        ImageView imageView = findViewById(R.id.progileImage);


        if (urlPhoto != null) {
//            Picasso.get().load(urlPhoto).placeholder(getResources().getDrawable(R.drawable.circle_gray)).memoryPolicy(MemoryPolicy.NO_CACHE).transform(new CircleTransform()).into(imageView);
        }

        loginView.setText(login);
        nameView.setText(name);

        try {
            loginView.setText(CachedValues.getLogin(this));
            nameView.setText(CachedValues.getDisplayName(this));
            bioView.setText(CachedValues.getBio(this));
            statusView.setText(CachedValues.getStatus(this));
        } catch (NotCachedException e) {
            e.printStackTrace();
        }

        SliderActivity sliderActivity = new SliderActivity();
        sliderActivity.attachSlider(this);
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void applyChanges(View v) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EditResponse editResponse = (new EditRequest(CachedValues.getSessionKey(EditProfileActivity.this),
                            String.valueOf(nameView.getText()),
                            String.valueOf(statusView.getText()),
                            String.valueOf(bioView.getText())
                            )).execute();
                    if (editResponse.isSuccess()) {
                        finish();
                    }
                } catch (ResponseException e) {
                    e.printStackTrace();
                } catch (NotCachedException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
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
//                ImageView imageView = findViewById(R.id.progileImage);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
                image = photoFile;
                Thread upload = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UploadImageResponse uploadImageResponse = (new UploadImageRequest(image, CachedValues.getSessionKey(getApplicationContext()))).execute();
                            String mediaId = uploadImageResponse.getMediaId();
                            ChangeAvatarResponse changeAvatarResponse = (new ChangeAvatarRequest(CachedValues.getSessionKey(getApplicationContext()), mediaId)).execute();
                            if(changeAvatarResponse.isSuccess())
                            {
                                EditProfileActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        CuteToast.showSuccess("uspeshno!", EditProfileActivity.this);
                                    }
                                });
                            }
                        } catch (ResponseException | NotCachedException e) {
                            e.printStackTrace();
                        }
                        catch (final Exception e)
                        {
                            e.printStackTrace();
                            EditProfileActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    CuteToast.showError(e.getMessage(), EditProfileActivity.this);
                                    finish();
                                }
                            });


                        }
                    }
                });
                upload.start();
                int dp = Numbers.dpToPx(150, getApplicationContext());
//                imageView.setImageBitmap(ImagesWorker.getCircleCroppedBitmap(bitmap, dp, dp));

            } catch (Exception e) {
                Log.d("ACTIVITYRES", "onActivityResult: " + e.toString());
            }
        }
    }
}