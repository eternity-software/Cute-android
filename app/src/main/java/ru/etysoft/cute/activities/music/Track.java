package ru.etysoft.cute.activities.music;

public class Track {

    private String name;
    private String artist;
    private String url;

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public String getUrl() {
        return url;
    }

    public Track(String name, String artist, String url) {
        this.name = name;
        this.artist = artist;
        this.url = url;
    }
}
