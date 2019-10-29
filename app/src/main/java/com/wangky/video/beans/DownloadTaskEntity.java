package com.wangky.video.beans;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.Date;

public class DownloadTaskEntity implements Serializable {
    private int id;
    private long taskId;
    private int mTaskStatus;
    private long mFileSize;
    private String mFileName;
    private int taskType;
    private String url;
    private String localPath;
    private long mDownloadSize;
    private long mDownloadSpeed;
    private long mDCDNSpeed;
    private String hash;
    private Boolean isFile;
    private Date createDate;
    private String thumbnailPath;
    private Bitmap thumbnail;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public int getmTaskStatus() {
        return mTaskStatus;
    }

    public void setmTaskStatus(int mTaskStatus) {
        this.mTaskStatus = mTaskStatus;
    }

    public long getmFileSize() {
        return mFileSize;
    }

    public void setmFileSize(long mFileSize) {
        this.mFileSize = mFileSize;
    }

    public String getmFileName() {
        return mFileName;
    }

    public void setmFileName(String mFileName) {
        this.mFileName = mFileName;
    }

    public int getTaskType() {
        return taskType;
    }

    public void setTaskType(int taskType) {
        this.taskType = taskType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public long getmDownloadSize() {
        return mDownloadSize;
    }

    public void setmDownloadSize(long mDownloadSize) {
        this.mDownloadSize = mDownloadSize;
    }

    public long getmDownloadSpeed() {
        return mDownloadSpeed;
    }

    public void setmDownloadSpeed(long mDownloadSpeed) {
        this.mDownloadSpeed = mDownloadSpeed;
    }

    public long getmDCDNSpeed() {
        return mDCDNSpeed;
    }

    public void setmDCDNSpeed(long mDCDNSpeed) {
        this.mDCDNSpeed = mDCDNSpeed;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Boolean getFile() {
        return isFile;
    }

    public void setFile(Boolean file) {
        isFile = file;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }
}
