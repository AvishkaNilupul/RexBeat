package org.openjfx.rexpotify.models;

public class Song {
    private String songID;
    private String songName;
    private String songLink;
    private  String songSearch;
    private Artist artist;  // Assuming you have an Artist class
    private String sdLink;

    public String getSongID() {
        return songID;
    }

    public void setSongID(String songID) {
        this.songID = songID;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getSongLink() {
        return songLink;
    }

    public void setSongLink(String songLink) {
        this.songLink = songLink;
    }

    public Artist getArtist() {
        return artist;
    }

    public void setArtist(Artist artist) {
        this.artist = artist;
    }

    public String getSongSearch() {
        return songSearch;
    }

    public void setSongSearch(String songSearch) {
        this.songSearch = songSearch;
    }

    public String getSdLink() {
        return sdLink;
    }

    public void setSdLink(String sdLink) {
        this.sdLink = sdLink;
    }
    // Constructors, getters, setters, etc.

    // You need to define the Artist class or use a String for the artist field, depending on your JSON structure.
}

