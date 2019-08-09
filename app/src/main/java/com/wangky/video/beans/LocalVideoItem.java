package com.wangky.video.beans;

public class LocalVideoItem {

    public LocalVideoItem(long id, String title, String data, long duration, String artist, long addTime, long size, long width, long height,String cover) {
        this.id = id;
        this.title = title;
        this.data = data;
        this.duration = duration;
        this.artist = artist;
        this.addTime = addTime;
        this.size = size;
        this.width = width;
        this.height = height;
        this.coverImg = cover;
    }

    private long id;

    private String title;

    private String data;

    private long duration;

    private String artist;

    private long addTime;

    private long size;

    private long width;

    private long height;

    private String coverImg;

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getAddTime() {
        return addTime;
    }

    public void setAddTime(long addTime) {
        this.addTime = addTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }


    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }
}
