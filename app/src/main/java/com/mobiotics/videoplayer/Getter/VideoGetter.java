package com.mobiotics.videoplayer.Getter;

public class VideoGetter {

    private int id;
    private String title;
    private String description;
    private String thumb;
    private String url;

    public VideoGetter(int id, String title, String thumb, String description, String url) {
        this.id = id;
        this.title = title;
        this.thumb = thumb;
        this.description = description;
        this.url = url;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumb() {
        return thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
