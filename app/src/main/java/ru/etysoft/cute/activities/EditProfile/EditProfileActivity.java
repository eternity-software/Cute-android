package ru.etysoft.cute.activities.EditProfile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.Avatar;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.components.LightToolbar;
import ru.etysoft.cute.data.CachedValues;
import ru.etysoft.cute.exceptions.NotCachedException;
import ru.etysoft.cute.utils.CircleTransform;
import ru.etysoft.cute.utils.Numbers;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.account.ChangeAvatar.ChangeAvatarRequest;
import ru.etysoft.cuteframework.methods.account.ChangeAvatar.ChangeAvatarResponse;
import ru.etysoft.cuteframework.methods.account.ChangeCover.ChangeCoverRequest;
import ru.etysoft.cuteframework.methods.account.ChangeCover.ChangeCoverResponse;
import ru.etysoft.cuteframework.methods.account.Edit.EditRequest;
import ru.etysoft.cuteframework.methods.account.Edit.EditResponse;
import ru.etysoft.cuteframework.methods.media.UploadImageRequest;
import ru.etysoft.cuteframework.methods.media.UploadImageResponse;
import ru.etysoft.cuteframework.requests.attachements.ImageFile;

public class EditProfileActivity extends AppCompatActivity implements EditProfileContract.View{

    private final int REQUEST_TAKE_PHOTO_FROM_GALLERY = 1;
    private final int REQUEST_TAKE_COVER_FROM_GALLERY = 2;
    private String id, name, login, urlPhoto, urlcover;
    private LightToolbar toolbar;
    private String mCurrentPhotoPath;
    private ImageFile image = null;
    private Avatar avatar;
    private ImageView cover;
    private TextView nameView, statusView, bioView, loginView, changeCover;
    private EditProfilePresenter editProfilePresenter;

    private final boolean isImageUpdated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        editProfilePresenter = new EditProfilePresenter(EditProfileActivity.this, this);
        initializeViews();
        setAccountInfo();
        SliderActivity sliderActivity = new SliderActivity();
        sliderActivity.attachSlider(this);
        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);
    }



    @Override
    public void initializeViews() {
        nameView = findViewById(R.id.editTextAccountName);
        statusView = findViewById(R.id.statusView);
        bioView = findViewById(R.id.bioView);
        toolbar = findViewById(R.id.toolbar);
        toolbar.animateAppear(findViewById(R.id.toolbarContainer));
        loginView = findViewById(R.id.editTextAccountLogin);
        avatar = findViewById(R.id.avatarView);
        changeCover = findViewById(R.id.changeCover);
        cover = findViewById(R.id.coverView);
    }

    @Override
    public void setAccountInfo() {
        try {
            urlPhoto = CachedValues.getAvatar(this);
            loginView.setText(CachedValues.getLogin(this));
            nameView.setText(CachedValues.getDisplayName(this));
            bioView.setText(CachedValues.getBio(this));
            statusView.setText(CachedValues.getStatus(this));
            urlcover = CachedValues.getCover(this);
            if (urlPhoto != null){
                Picasso.get().load(urlPhoto).placeholder(getResources().getDrawable(R.drawable.circle_gray)).transform(new CircleTransform()).into(avatar.getPictureView());
            }
            if (urlcover != null){
                Picasso.get().load(urlcover).into(cover);
            }
            if (urlcover == null){
                changeCover.setBackground(getResources().getDrawable(R.drawable.square_rounded_corners_gray));
            }
        }catch (NotCachedException e){
            e.printStackTrace();
        }
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
                            String.valueOf(bioView.getText()),
                            String.valueOf(loginView.getText())
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

    public void updateAvatar(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_TAKE_PHOTO_FROM_GALLERY);
    }

    public void updateCover(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_TAKE_COVER_FROM_GALLERY);
    }

    public void back(View v) {
        onBackPressed();
    }

    @Override
    protected void onActivityResult(final int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_TAKE_PHOTO_FROM_GALLERY | requestCode == REQUEST_TAKE_COVER_FROM_GALLERY)  && resultCode == RESULT_OK) {
            try {
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
                            if(requestCode == REQUEST_TAKE_PHOTO_FROM_GALLERY) {
                                ChangeAvatarResponse changeAvatarResponse = (new ChangeAvatarRequest(CachedValues.getSessionKey(getApplicationContext()), mediaId)).execute();
                                if (changeAvatarResponse.isSuccess()) {
                                    EditProfileActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            CuteToast.showSuccess("uspeshno!", EditProfileActivity.this);
                                        }
                                    });
                                }
                            }
                            else if(requestCode == REQUEST_TAKE_COVER_FROM_GALLERY)
                            {
                                ChangeCoverResponse changeCoverResponse = (new ChangeCoverRequest(CachedValues.getSessionKey(getApplicationContext()), mediaId)).execute();
                                if (changeCoverResponse.isSuccess()) {
                                    EditProfileActivity.this.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            CuteToast.showSuccess("uspeshno shapka!", EditProfileActivity.this);
                                        }
                                    });
                                }
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