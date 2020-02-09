package com.wangky.video.beans;

import com.wangky.video.enums.FileTyp;

public class LocalFile {

    /**
     * 文件名称
     */
    private String name;

    /**
     * 文件类型
     * 1：目录
     * 2：文件
     */
    private FileTyp type;


    /**
     * 文件修改时间
     *
     */
    private long modifyTime;


    /**
     * 文件大小
     */
    private long fileSize;


    /**
     * 文件路径
     */
    private String filePath;


    public LocalFile(){


    }

    public LocalFile(String name, FileTyp type, long modifyTime, long fileSize, String filePath) {
        this.name = name;
        this.type = type;
        this.modifyTime = modifyTime;
        this.fileSize = fileSize;
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FileTyp getType() {
        return type;
    }

    public void setType(FileTyp type) {
        this.type = type;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
}
