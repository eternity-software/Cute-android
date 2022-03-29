package ru.etysoft.cute.activities.music;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.etysoft.cute.R;

import ru.etysoft.cute.media.MediaActions;
import ru.etysoft.cute.media.MediaService;

public class MusicAdapter extends RecyclerView.Adapter<MusicAdapter.TracksViewHolder> {
    private final Activity context;
    private final List<Track> list;
    public static boolean isMessagingActivityOpened = true;
    private LayoutInflater layoutInflater;

    public MusicAdapter(Activity context, List<Track> values) {
        this.context = context;
        this.list = values;
        layoutInflater = LayoutInflater.from(context);
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
            final Track info = list.get(position);


            // Задаём обработчик нажатия
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MediaActions.sendAction(MediaActions.ACTION_PLAY, view.getContext());
                            MediaService.setTrackList(list);
                            MediaService.play(info, null);

                        }
                    });
                    thread.start();



                }
            });


            holder.nameView.setText(info.getName());
            holder.artistView.setText(info.getArtist());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public List<Track> getList() {
        return list;
    }

    // Держим данные
    public static class TracksViewHolder extends RecyclerView.ViewHolder {
        TextView nameView;
        TextView artistView;
        View mainView;

        public TracksViewHolder(@NonNull View view) {
            super(view);
            mainView = view;
            artistView = view.findViewById(R.id.artistNameView);
            nameView = view.findViewById(R.id.trackNameView);
        }


    }
}

