package ru.etysoft.cute.activities.camera;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.otaliastudios.cameraview.BitmapCallback;
import com.otaliastudios.cameraview.CameraListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.PictureResult;
import com.otaliastudios.cameraview.controls.Engine;
import com.otaliastudios.cameraview.controls.Facing;
import com.otaliastudios.cameraview.frame.Frame;
import com.otaliastudios.cameraview.frame.FrameProcessor;
import com.otaliastudios.cameraview.size.Size;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.activities.camera.face.FaceTracker;
import ru.etysoft.cute.utils.Logger;

public class CameraActivity extends AppCompatActivity {
    private boolean isFrontFacing = true;

    private static final String TAG = "FaceActivity";
    public static boolean isWorking = false;

    private GraphicOverlay graphicOverlay;
    private CameraView cameraView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        cameraView = findViewById(R.id.cameraView);
        graphicOverlay = (GraphicOverlay) findViewById(R.id.faceOverlay);

        final ImageButton button = (ImageButton) findViewById(R.id.flipButton);
        button.setOnClickListener(switchCameraButtonListener);
        cameraView.setFacing(Facing.FRONT);

        cameraView.setSnapshotMaxHeight(100);
        cameraView.setPreviewFrameRate(10);
        cameraView.setEngine(Engine.CAMERA1);


        final FaceTracker faceTracker = (new FaceTracker(graphicOverlay, getApplicationContext(), false));
        graphicOverlay.setmFrontFacing(false);
        cameraView.setLifecycleOwner(this);
        cameraView.setFrameProcessingExecutors(5);
        cameraView.setFrameProcessingPoolSize(6);




        cameraView.addFrameProcessor(new FrameProcessor() {
            @Override
            public void process(@NonNull final Frame frame) {
                try {
                    final int height = cameraView.getHeight();
                    final int width = cameraView.getWidth();
                    final ArrayList<String> debugStrings = new ArrayList<>();
                    final long startTime = System.currentTimeMillis();
                    final Bitmap[] bitmap = new Bitmap[1];
                    if (frame.getDataClass() == byte[].class) {
                        byte[] data = frame.getData();
                        Size s = frame.getSize();
                        YuvImage yuv = new YuvImage(data, ImageFormat.NV21, s.getWidth(), s.getHeight(), null);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        yuv.compressToJpeg(new Rect(0, 0, s.getWidth(), s.getHeight()), 100, stream);
                        byte[] buf = stream.toByteArray();

                        bitmap[0] = BitmapFactory.decodeByteArray(buf, 0, buf.length, null);
                        System.out.println("Processing byte!");
                    } else if (frame.getDataClass() == Image.class) {
                        Image data = frame.getData();

                        ByteBuffer buffer = data.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.capacity()];
                        buffer.get(bytes);
                        bitmap[0] = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, null);
                        System.out.println("Processing image!");
                    }
                    else
                    {
                        return;
                    }

                    if(isWorking) return;
//                    Thread thread = new Thread(new Runnable() {
//                        @Override
//                        public void run() {



                    long timeImageDecode =  (System.currentTimeMillis() - startTime);

                    debugStrings.add("bitmapdecode: " + timeImageDecode + " ms");

                    final long startTimePostProcessing = System.currentTimeMillis();



                    bitmap[0] = RotateBitmap(bitmap[0], 90);
                    bitmap[0] = mirrorBitmap(bitmap[0]);





                    float prop = (float) width / height;

                    int newHeight = (bitmap[0].getHeight());
                    int newWidth = (int) (bitmap[0].getHeight() *  prop);

                    //bitmap = loadBitmapFromView(previewView);


                    bitmap[0] = Bitmap.createBitmap(
                            bitmap[0],
                            bitmap[0].getWidth() / 2 - newWidth /2,
                            0,
                            newWidth,
                            newHeight
                    );

                    int compressFactor = 10;
                     // Instad compressing change CameraView preview size
                    //  bitmap[0] = compressBitmap(bitmap[0], compressFactor);



                    graphicOverlay.setmHeightScaleFactor((float)height / bitmap[0].getHeight());
                    graphicOverlay.setmWidthScaleFactor((float)width / bitmap[0].getWidth());




                    long postProcessingTime = (System.currentTimeMillis() - startTimePostProcessing);
                    debugStrings.add("post_processing: " + postProcessingTime + " ms");
                    isWorking = true;
                    final long startFaceDetectionTime = (System.currentTimeMillis());
                    FaceDetectorOptions options =
                            new FaceDetectorOptions.Builder()
                                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                                    .enableTracking()
                                    .build();
                    InputImage image = InputImage.fromBitmap(bitmap[0], 0);
                    FaceDetector faceDetector = FaceDetection.getClient(options);
                    Task<List<Face>> faces =
                            faceDetector.process(image)
                                    .addOnSuccessListener(
                                            new OnSuccessListener<List<Face>>() {
                                                @Override
                                                public void onSuccess(List<Face> faces) {
                                                    long faceDetectionTime = (System.currentTimeMillis() - startFaceDetectionTime);
                                                    debugStrings.add("face_det: " + faceDetectionTime + " ms");
                                                    if(faces.size() == 0)
                                                    {
                                                        faceTracker.update(null);
                                                        isWorking = false;
                                                    }
                                                    else
                                                    {
                                                        debugStrings.add( "face_count: " + faces.size());
                                                        Logger.logActivity("Processing graphic (face rec took " + (System.currentTimeMillis() - startTime)+"ms");
                                                        faceTracker.update(faces,  debugStrings);
                                                    }



                                                }
                                            })
                                    .addOnFailureListener(
                                            new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    isWorking = false;

                                                }
                                            });
//
//                        }
//                    });
//                    thread.start();


                }
                catch (Exception e)
                {
                    isWorking = false;
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasF) {
        super.onWindowFocusChanged(hasF);


    }

    public static Bitmap loadBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static Bitmap compressBitmap(Bitmap bmOriginal, int compressFactor)
    {
        int width = bmOriginal.getWidth();
        int height = bmOriginal.getHeight();

        int halfWidth = width / compressFactor;
        int halfHeight = height / compressFactor;

        return Bitmap.createScaledBitmap(bmOriginal, halfWidth,
                halfHeight, false);
    }

    public static Bitmap mirrorBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.preScale(1.0f, -1.0f);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        return newBitmap;
    }

    private View.OnClickListener switchCameraButtonListener = new View.OnClickListener() {
        public void onClick(View v) {
            cameraView.takePicture();


        }
    };


}