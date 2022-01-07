package ru.etysoft.cute.activities.camera.face;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceLandmark;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.etysoft.cute.activities.camera.CameraActivity;
import ru.etysoft.cute.activities.camera.GraphicOverlay;


class FaceGraphic extends GraphicOverlay.Graphic {

    private static final String TAG = "FaceGraphic";

    private static final float DOT_RADIUS = 3.0f;
    private static final float TEXT_OFFSET_Y = -30.0f;

    private boolean mIsFrontFacing;

    // This variable may be written to by one of many threads. By declaring it as volatile,
    // we guarantee that when we read its contents, we're reading the most recent "write"
    // by any thread.
    private volatile List<Face> faces = new ArrayList<>();

    private Paint mHintTextPaint;
    private Paint mHintOutlinePaint;
    private Paint mEyeWhitePaint;
    private Paint mIrisPaint;
    private Paint mEyeOutlinePaint;
    private Context context;
    private Paint mEyelidPaint;

    private Drawable mPigNoseGraphic;
    private Drawable mMustacheGraphic;
    private Drawable mHappyStarGraphic;
    private Drawable mHatGraphic;
    private GraphicOverlay graphicOverlay;

    private List<FaceData> faceDataList = new ArrayList<>();
    private ArrayList<String> debugStrings;

    FaceGraphic(GraphicOverlay overlay, Context context, boolean isFrontFacing) {
        super(overlay);
        mIsFrontFacing = isFrontFacing;
        Resources resources = context.getResources();
        initializePaints(resources);
        this.context = context;
        initializeGraphics(resources);

        this.graphicOverlay = overlay;
        overlay.add(this);
    }




    private void initializeGraphics(Resources resources) {
//        mPigNoseGraphic = resources.getDrawable(R.drawable.pig_nose_emoji);
//        mMustacheGraphic = resources.getDrawable(R.drawable.mustache);
//        mHappyStarGraphic = resources.getDrawable(R.drawable.happy_star);
//        mHatGraphic = resources.getDrawable(R.drawable.red_hat);
    }

    private void initializePaints(Resources resources) {
        mHintTextPaint = new Paint();

        int color = Color.MAGENTA;

        mHintTextPaint.setColor(color);
        mHintTextPaint.setTextSize(40);

        mHintOutlinePaint = new Paint();
        mHintOutlinePaint.setColor(color);
        mHintOutlinePaint.setStyle(Paint.Style.STROKE);
        mHintOutlinePaint.setStrokeWidth(10);

        mEyeWhitePaint = new Paint();
        mEyeWhitePaint.setColor(color);
        mEyeWhitePaint.setStyle(Paint.Style.FILL);

        mIrisPaint = new Paint();
        mIrisPaint.setColor(color);
        mIrisPaint.setStyle(Paint.Style.FILL);

        mEyeOutlinePaint = new Paint();
        mEyeOutlinePaint.setColor(color);
        mEyeOutlinePaint.setStyle(Paint.Style.STROKE);
        mEyeOutlinePaint.setStrokeWidth(10);

        mEyelidPaint = new Paint();
        mEyelidPaint.setColor(color);
        mEyelidPaint.setStyle(Paint.Style.FILL);
    }

    void update(List<Face> faces, ArrayList<String> debugStrings) {
        faceDataList.clear();

        this.faces = faces;

        if(faces != null) {
            for (Face face : faces) {


                // Get face dimensions.


                // Get the positions of facial landmarks.

                FaceData mFaceData = new FaceData();

                mFaceData.setLeftEyePosition(getLandmarkPosition(face, FaceLandmark.LEFT_EYE));
                mFaceData.setRightEyePosition(getLandmarkPosition(face, FaceLandmark.RIGHT_EYE));
                mFaceData.setMouthBottomPosition(getLandmarkPosition(face, FaceLandmark.LEFT_CHEEK));
                mFaceData.setMouthBottomPosition(getLandmarkPosition(face, FaceLandmark.RIGHT_CHEEK));
                mFaceData.setNoseBasePosition(getLandmarkPosition(face, FaceLandmark.NOSE_BASE));
                mFaceData.setMouthBottomPosition(getLandmarkPosition(face, FaceLandmark.LEFT_EAR));

                mFaceData.setMouthBottomPosition(getLandmarkPosition(face, FaceLandmark.RIGHT_EAR));

                mFaceData.setMouthLeftPosition(getLandmarkPosition(face, FaceLandmark.MOUTH_LEFT));
                mFaceData.setMouthBottomPosition(getLandmarkPosition(face, FaceLandmark.MOUTH_BOTTOM));
                mFaceData.setMouthRightPosition(getLandmarkPosition(face, FaceLandmark.MOUTH_RIGHT));

                this.debugStrings = debugStrings;
                faceDataList.add(mFaceData);
            }
        }
        postInvalidate(); // Trigger a redraw of the graphic (i.e. cause draw() to be called).
    }



    private PointF getLandmarkPosition(Face face, int landmarkId) {

        if(face == null) return null;

        for (FaceLandmark landmark : face.getAllLandmarks()) {
            if (landmark.getLandmarkType() == landmarkId) {

                return landmark.getPosition();
            }
        }


            return null;



//        float x = face.getBoundingBox().centerX() + (landmarkPosition.x * face.getBoundingBox().width());
//        float y = face.getBoundingBox().centerY() + (landmarkPosition.y * face.getBoundingBox().height());
//        return new PointF(x, y);
    }

    @Override
    public void draw(Canvas canvas) {
        final float DOT_RADIUS = 3.0f;
        final float TEXT_OFFSET_Y = -30.0f;
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        // Confirm that the face and its features are still visible before drawing any graphics over it.
        if (faces == null) {
            canvas.drawText("face not found " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()), 20, 50, mHintTextPaint);
            CameraActivity.isWorking = false;
            return;
        }
        if (faces.size() == 0) {
            canvas.drawText("face not found " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()), 20, 50, mHintTextPaint);
            CameraActivity.isWorking = false;
            return;
        }


        // 1
        canvas.drawText("cute debug mode " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()), 20, 50, mHintTextPaint);
        int startDebugY = 50;
        for(String debugString : debugStrings)
        {
            startDebugY += 60;
            canvas.drawText(debugString, 20, startDebugY, mHintTextPaint);

        }


        for(FaceData mFaceData : faceDataList) {

            PointF detectLeftEyePosition = mFaceData.getLeftEyePosition();
            PointF detectRightEyePosition = mFaceData.getRightEyePosition();
            PointF detectNoseBasePosition = mFaceData.getNoseBasePosition();
            PointF detectMouthLeftPosition = mFaceData.getMouthLeftPosition();
            PointF detectMouthBottomPosition = mFaceData.getMouthBottomPosition();
            PointF detectMouthRightPosition = mFaceData.getMouthRightPosition();

            //  detectRightEyePosition == null ||
            //        detectNoseBasePosition == null || detectMouthLeftPosition == null || detectMouthBottomPosition == null || detectMouthRightPosition == null


            if (detectLeftEyePosition == null) {
                canvas.drawText("face not found " + new SimpleDateFormat("HH:mm:ss.SSS").format(new Date()), 20, 50, mHintTextPaint);
                CameraActivity.isWorking = false;
                return;
            }

            // 2
            float leftEyeX = detectLeftEyePosition.x * graphicOverlay.getmWidthScaleFactor();
            float leftEyeY = detectLeftEyePosition.y * graphicOverlay.getmHeightScaleFactor();


            if (mIsFrontFacing) {
                leftEyeY = graphicOverlay.getWidth() - leftEyeY;
            }

            canvas.drawCircle(leftEyeX, leftEyeY, DOT_RADIUS, mHintOutlinePaint);
            canvas.drawText("left eye ", leftEyeX, leftEyeY + TEXT_OFFSET_Y, mHintTextPaint);


            float rightEyeX = detectRightEyePosition.x * graphicOverlay.getmWidthScaleFactor();
            ;
            float rightEyeY = detectRightEyePosition.y * graphicOverlay.getmHeightScaleFactor();
            canvas.drawCircle(rightEyeX, rightEyeY, DOT_RADIUS, mHintOutlinePaint);
            canvas.drawText("right eye", rightEyeX, rightEyeY + TEXT_OFFSET_Y, mHintTextPaint);

            float noseBaseX = detectNoseBasePosition.x * graphicOverlay.getmWidthScaleFactor();
            ;
            float noseBaseY = detectNoseBasePosition.y * graphicOverlay.getmHeightScaleFactor();
            canvas.drawCircle(noseBaseX, noseBaseY, DOT_RADIUS, mHintOutlinePaint);
            canvas.drawText("nose base", noseBaseX, noseBaseY + TEXT_OFFSET_Y, mHintTextPaint);

            float mouthLeftX = detectMouthLeftPosition.x * graphicOverlay.getmWidthScaleFactor();
            ;
            float mouthLeftY = detectMouthLeftPosition.y * graphicOverlay.getmHeightScaleFactor();
            canvas.drawCircle(mouthLeftX, mouthLeftY, DOT_RADIUS, mHintOutlinePaint);
            canvas.drawText("mouth left", mouthLeftX, mouthLeftY + TEXT_OFFSET_Y, mHintTextPaint);

            float mouthRightX = detectMouthRightPosition.x * graphicOverlay.getmWidthScaleFactor();
            ;
            float mouthRightY = detectMouthRightPosition.y * graphicOverlay.getmHeightScaleFactor();
            canvas.drawCircle(mouthRightX, mouthRightY, DOT_RADIUS, mHintOutlinePaint);
            canvas.drawText("mouth right", mouthRightX, mouthRightY + TEXT_OFFSET_Y, mHintTextPaint);

            float mouthBottomX = detectMouthBottomPosition.x * graphicOverlay.getmWidthScaleFactor();
            ;
            float mouthBottomY = detectMouthBottomPosition.y * graphicOverlay.getmHeightScaleFactor();
            canvas.drawCircle(mouthBottomX, mouthBottomY, DOT_RADIUS, mHintOutlinePaint);
            canvas.drawText("mouth bottom", mouthBottomX, mouthBottomY + TEXT_OFFSET_Y, mHintTextPaint);
            CameraActivity.isWorking = false;
        }
    }

}
