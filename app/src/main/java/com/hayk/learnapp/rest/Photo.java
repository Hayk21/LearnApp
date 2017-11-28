package com.hayk.learnapp.rest;

/**
 * Created by User on 13.11.2017.
 */

public class Photo {
    private String albumId;
    private String id;
    private String title;
    private String url;
    private String thumbnailUrl;

    public Photo(String albumId, String id, String title, String url, String thumbnailUrl) {
        this.albumId = albumId;
        this.id = id;
        this.title = title;
        this.url = url;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }
}
