package ru.etysoft.cute.activities.camera.face;

import android.content.Context;
import android.graphics.PointF;

import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceLandmark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.etysoft.cute.activities.camera.GraphicOverlay;

public class FaceTracker {

    private static final String TAG = "FaceTracker";

    private GraphicOverlay mOverlay;
    private Context mContext;
    private boolean mIsFrontFacing;
    private FaceGraphic mFaceGraphic;
    private Face mFace;

    // Subjects may move too quickly to for the system to detect their detect features,
    // or they may move so their features are out of the tracker's detection range.
    // This map keeps track of previously detected facial landmarks so that we can approximate
    // their locations when they momentarily "disappear".
    private Map<Integer, PointF> mPreviousLandmarkPositions = new HashMap<>();

    public FaceTracker(GraphicOverlay overlay, Context context, boolean isFrontFacing) {
        mOverlay = overlay;
        mContext = context;
        mIsFrontFacing = isFrontFacing;

        mFaceGraphic = new FaceGraphic(mOverlay, mContext, mIsFrontFacing);

    }

    public void setmFace(Face mFace) {
        this.mFace = mFace;
    }

    public void update(List<Face> faces)
    {
        mFaceGraphic.update(faces, null);
    }

    public void update(List<Face> faces, ArrayList<String> debugStrings)
    {
        mFaceGraphic.update(faces, debugStrings);
    }

    // Facial landmark utility methods
    // ===============================

    /** Given a face and a facial landmark position,
     *  return the coordinates of the landmark if known,
     *  or approximated coordinates (based on prior data) if not.
     */
    private PointF getLandmarkPosition(Face face, int landmarkId) {
        for (FaceLandmark landmark : face.getAllLandmarks()) {
            if (landmark.getLandmarkType() == landmarkId) {
                return landmark.getPosition();
            }
        }

        PointF landmarkPosition = mPreviousLandmarkPositions.get(landmarkId);
        if (landmarkPosition == null) {
            return null;
        }


        float x = face.getBoundingBox().centerX() + (landmarkPosition.x * face.getBoundingBox().width());
        float y = face.getBoundingBox().centerY() + (landmarkPosition.y * face.getBoundingBox().height());
        return new PointF(x, y);
    }

    private void updatePreviousLandmarkPositions(Face face) {
        for (FaceLandmark landmark : face.getAllLandmarks()) {
            PointF position = landmark.getPosition();
            float xProp = (position.x - face.getBoundingBox().centerX()) / face.getBoundingBox().width();
            float yProp = (position.y - face.getBoundingBox().centerY()) / face.getBoundingBox().height();
            mPreviousLandmarkPositions.put(landmark.getLandmarkType(), new PointF(xProp, yProp));
        }
    }
}