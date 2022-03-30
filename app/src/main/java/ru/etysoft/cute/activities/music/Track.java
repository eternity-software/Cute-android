package ru.etysoft.cute.activities.music;

import ru.etysoft.cuteframework.models.TrackInfo;

public class Track {

    private String name;
    private String artist;
    private String url;
    private String cover;
    private String id;

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getUrl() {
        return url;
    }

    public String getId() {
        return id;
    }

    public Track(String name, String artist, String url, String cover) {
        this.name = name;
        this.artist = artist;
        this.url = url;
        this.cover = cover;
    }

    public String getCover() {
        return cover;
    }

    public Track(TrackInfo track) {
        this.name = track.getName();
        this.artist = track.getArtist();
        this.url = track.getPath();
        this.cover = track.getCover();
        this.id = track.getId();
    }
}
