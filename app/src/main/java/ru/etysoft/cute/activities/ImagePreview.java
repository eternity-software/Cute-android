package ru.etysoft.cute.activities;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.SharedElementCallback;

import com.github.chrisbanes.photoview.OnOutsidePhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Objects;

import ru.etysoft.cute.R;
import ru.etysoft.cute.components.CuteToast;
import ru.etysoft.cute.images.ImagesWorker;
import ru.etysoft.cute.utils.Numbers;

public class ImagePreview extends AppCompatActivity {

    public static final String EXTRA_CLIP_RECT = "rect";
    private CardView card;
    private boolean isShown = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        String photo = String.valueOf(getIntent().getExtras().get("url"));
        PhotoView photoView = findViewById(R.id.photoView);
        Picasso.get().load(photo).into(photoView);
        photoView.setOnOutsidePhotoTapListener(new OnOutsidePhotoTapListener() {
            @Override
            public void onOutsidePhotoTap(ImageView imageView) {
                onBackPressed();
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {



           // getWindow().setEnterTransition(null);

            // Setup transition
            Transition transition =
                    TransitionInflater.from(this)
                            .inflateTransition(R.transition.shared_element_transition);
            transition.setDuration(400);
            transition.setInterpolator(new DecelerateInterpolator(2f));

            getResources().getString(R.string.transition_image_preview);
            getWindow().setSharedElementEnterTransition(transition);

            // Postpone the transition. We will start it when the slideshow is ready.
           // ActivityCompat.postponeEnterTransition(this);

            final String transitionName = getIntent().getStringExtra(getResources().getString(R.string.transition_image_preview));
            final Rect clipRect = getIntent().getParcelableExtra(EXTRA_CLIP_RECT);
            setEnterSharedElementCallback(new SharedElementCallback() {
                @Override
                public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                    for (int i = 0; i < sharedElementNames.size(); i++) {
                        if (Objects.equals(transitionName, sharedElementNames.get(i))) {
                            View view = sharedElements.get(i);
                            view.setClipBounds(clipRect);
                        }
                    }
                    super.onSharedElementStart(sharedElementNames, sharedElements, sharedElementSnapshots);
                }

                @Override
                public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
                    for (int i = 0; i < sharedElementNames.size(); i++) {
                        if (Objects.equals(transitionName, sharedElementNames.get(i))) {
                            View view = sharedElements.get(i);
                            view.setClipBounds(null);
                        }
                    }
                    super.onSharedElementEnd(sharedElementNames, sharedElements, sharedElementSnapshots);
                }
            });
            card = findViewById(R.id.imageContainer);
            getWindow().getSharedElementEnterTransition()
                    .addListener(new Transition.TransitionListener() {
                        @Override
                        public void onTransitionStart(Transition transition) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                                ObjectAnimator animator;
                                if(isShown)
                                {

                                    animator = ObjectAnimator.ofFloat(card, "radius", Numbers.dpToPx(12, getApplicationContext()));

                                }
                                else
                                {
                                    isShown = true;
                                    animator = ObjectAnimator.ofFloat(card, "radius", Numbers.dpToPx(0, getApplicationContext()));

                                }

                                animator.setDuration(250);
                                animator.start();
                            }
                        }

                        @Override
                        public void onTransitionEnd(Transition transition) {

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
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }

    }

    public void save(View v) {
        PhotoView photoView = findViewById(R.id.photoView);
        Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
        ImagesWorker.saveBitmapToGallery(bitmap, this);
        CuteToast.show(getResources().getString(R.string.saved_to_gallery), R.drawable.icon_success, this);
    }

    public void exit(View v) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {

        overridePendingTransition(R.anim.fade_out, R.anim.fade_out);

        super.onBackPressed();
    }
}