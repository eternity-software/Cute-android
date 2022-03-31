package ru.etysoft.cute.activities.music;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.media.MediaActions;
import ru.etysoft.cute.media.MediaService;
import ru.etysoft.cuteframework.exceptions.NotCachedException;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.music.MusicGetTrackRequest;
import ru.etysoft.cuteframework.models.TrackInfo;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.TracksViewHolder> implements MediaService.MediaServiceCallback {
    private final Activity context;
    private final List<TrackInfo> list;
    public static boolean isMessagingActivityOpened = true;
    private LayoutInflater layoutInflater;

    public MusicAdapter(Activity context, List<TrackInfo> values) {
        this.context = context;
        this.list = values;
        layoutInflater = LayoutInflater.from(context);
        MediaService.addCallback(this);
    }


    @NonNull
    @Override
    public TracksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new TracksViewHolder((layoutInflater.inflate(R.layout.music_element, parent, false)));
    }

    @Override
    public void onBindViewHolder(@NonNull TracksViewHolder holder, int position) {
        View view = holder.mainView;

        try {
            // Инициализируем информацию о беседе или диалоге
            final TrackInfo info = list.get(position);


            // Задаём обработчик нажатия
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                MusicGetTrackRequest.MusicGetTrackResponse getTrackResponse = new MusicGetTrackRequest(info.getId()).execute();
                                MediaActions.sendAction(MediaActions.ACTION_PLAY, view.getContext());
                              //  ArrayList<TrackInfo> empty = new ArrayList<>();
                               // empty.add(getTrackResponse.getTrackInfo());

                                list.set(list.indexOf(info), getTrackResponse.getTrackInfo());
                                MediaService.setTrackList(new ArrayList<>(list));

                                if(info.getCover() != null)
                                {
                                    MediaService.play(getTrackResponse.getTrackInfo(), getBitmapFromURL(info.getCover()));
                                }
                                else
                                {
                                    MediaService.play(getTrackResponse.getTrackInfo(), null);
                                }
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        notifyDataSetChanged();
                                    }
                                });

                            } catch (ResponseException e) {
                                e.printStackTrace();
                            } catch (SQLException throwables) {
                                throwables.printStackTrace();
                            } catch (NotCachedException e) {
                                e.printStackTrace();
                            }


                        }
                    });
                    thread.start();



                }
            });


            holder.nameView.setText(info.getName());
            if(info.getCover() != null)
            {
                Picasso.get().load(info.getCover()).into(holder.coverView);
            }
            else
            {
                holder.coverView.setImageResource(R.drawable.empty_cover);
            }

            holder.artistView.setText(info.getArtist());

            if (MediaService.getTrackName().equals(info.getName()) && MediaService.getArtistName().equals(info.getArtist()) && MediaService.getArtistName() != null) {
                holder.isAnimated = true;
                holder.blackView.setVisibility(View.VISIBLE);
                holder.playIndicator.setVisibility(View.VISIBLE);
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 2f, 1.0f, 2f, 15f, 15f);

                scaleAnimation.setDuration(500);
                scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

                scaleAnimation.setRepeatMode(Animation.REVERSE);
                scaleAnimation.setRepeatCount(Animation.INFINITE);
                holder.playIndicator.startAnimation(scaleAnimation);

            }
            else
            {
                holder.isAnimated = false;
                holder.blackView.setVisibility(View.GONE);
                holder.playIndicator.setVisibility(View.GONE);
            }



        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onViewAttachedToWindow(@NonNull TracksViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if(holder.isAnimated)
        {
            holder.blackView.setVisibility(View.VISIBLE);
            holder.playIndicator.setVisibility(View.VISIBLE);
            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 2f, 1.0f, 2f, 15f, 15f);

            scaleAnimation.setDuration(500);
            scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());

            scaleAnimation.setRepeatMode(Animation.REVERSE);
            scaleAnimation.setRepeatCount(Animation.INFINITE);
            holder.playIndicator.startAnimation(scaleAnimation);
        }

    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<TrackInfo> getList() {
        return list;
    }

    @Override
    public void onTrackChanged(String name, String artist, Bitmap bitmap) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public void onServiceStopped() {

    }

    @Override
    public void onPlay() {

    }

    // Держим данные
    public static class TracksViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView artistView;
        View mainView;
        ImageView coverView;
        ImageView playIndicator;
        ImageView blackView;
        boolean isAnimated = false;

        public TracksViewHolder(@NonNull View view) {
            super(view);
            mainView = view;
            artistView = view.findViewById(R.id.artistNameView);
            nameView = view.findViewById(R.id.trackNameView);
            coverView = view.findViewById(R.id.coverView);
            playIndicator = view.findViewById(R.id.playIndicator);
            blackView = view.findViewById(R.id.blackView);
        }


    }
}

