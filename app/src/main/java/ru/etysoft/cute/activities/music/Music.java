package ru.etysoft.cute.activities.music;


import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.media.MediaService;
import ru.etysoft.cute.themes.Theme;
import ru.etysoft.cute.utils.SliderActivity;

public class Music extends AppCompatActivity {

    private RecyclerView tracksRecyclerView;
    private MusicAdapter musicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        SliderActivity sliderActivity = new SliderActivity();
        sliderActivity.attachSlider(this);

        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);

        tracksRecyclerView = findViewById(R.id.mainTrackList);


        List<Track> trackList = new ArrayList<>();

        trackList.add(new Track("Прыгай, за руки держась", "8(913)",
                "https://ru.muzikavsem.org/dl/276846446/8913_-_Prygajj_za_ruki_derzhas_(ru.muzikavsem.org).mp3"));

        trackList.add(new Track("Лил Пип Умер", "ПАНЦУШОТ",
                "https://ru.muzikavsem.org/dl/1757642664/PANCUSHOT_-_Lil_Pip_Umer_(ru.muzikavsem.org).mp3"));

        trackList.add(new Track("Не пошлая молли", "Lida",
                "https://ru.muzikavsem.org/dl/241965757/Lida_-_Ne_poshlaya_Molli_(ru.muzikavsem.org).mp3"));

        trackList.add(new Track("Киси Миси", "хрися, Hotzzen",
                "https://ru.muzikavsem.org/dl/453202660/Hotzzen_khrisya_-_Kisi_Misi_(ru.muzikavsem.org).mp3"));

        trackList.add(new Track("Порно пати", "Lida",
                "https://ru.muzikavsem.org/dl/881678811/Lida_-_Porno_pati_(ru.muzikavsem.org).mp3"));

        trackList.add(new Track("Молодёжная (ночь)", "Lida",
                "https://ru.muzikavsem.org/dl/1230391283/Lida_-_Molodjozhnaya_noch_(ru.muzikavsem.org).mp3"));

        trackList.add(new Track("Анапа", "Lida",
                "https://ru.muzikavsem.org/dl/2016495946/Lida_DK_-_Anapa_(ru.muzikavsem.org).mp3"));

        musicAdapter = new MusicAdapter(this, trackList);
        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tracksRecyclerView.setAdapter(musicAdapter);


        musicAdapter.notifyDataSetChanged();

        Theme.applyBackground(findViewById(R.id.rootView));
    }
}