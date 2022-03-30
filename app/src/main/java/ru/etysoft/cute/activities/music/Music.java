package ru.etysoft.cute.activities.music;


import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import ru.etysoft.cute.R;
import ru.etysoft.cute.media.MediaService;
import ru.etysoft.cute.themes.Theme;
import ru.etysoft.cute.utils.SliderActivity;
import ru.etysoft.cuteframework.exceptions.NotCachedException;
import ru.etysoft.cuteframework.exceptions.ResponseException;
import ru.etysoft.cuteframework.methods.music.MusicSearchRequest;
import ru.etysoft.cuteframework.models.TrackInfo;

public class Music extends AppCompatActivity {

    private RecyclerView tracksRecyclerView;
    private MusicAdapter musicAdapter;
    private EditText searchView;
    private List<TrackInfo> trackList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        SliderActivity sliderActivity = new SliderActivity();
        sliderActivity.attachSlider(this);

        overridePendingTransition(R.anim.slide_to_right, R.anim.slide_from_left);

        tracksRecyclerView = findViewById(R.id.mainTrackList);
        searchView = findViewById(R.id.searchBox);

        searchView.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {


                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event != null &&
                                        event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                            if (event == null || !event.isShiftPressed()) {
                                Thread thread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            MusicSearchRequest.MusicSearchResponse musicSearchResponse =
                                                    new MusicSearchRequest(searchView.getText().toString()).execute();

                                            trackList.clear();
                                            if(musicSearchResponse.isSuccess())
                                            {
                                                for(TrackInfo trackInfo : musicSearchResponse.getTrackInfos())
                                                {
                                                    System.out.println("info: " + trackInfo.getName() + " cover: " + trackInfo.getCover());
                                                    trackList.add(trackInfo);
                                                }
                                            }

                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    musicAdapter.notifyDataSetChanged();
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

                                return true; // consume.
                            }
                        }
                        return false; // pass on to other listeners.
                    }
                }
        );


        musicAdapter = new MusicAdapter(this, trackList);
        tracksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        tracksRecyclerView.setAdapter(musicAdapter);


        musicAdapter.notifyDataSetChanged();

        Theme.applyBackground(findViewById(R.id.rootView));
    }


}