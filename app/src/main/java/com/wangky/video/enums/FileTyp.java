package com.wangky.video.enums;

public enum FileTyp {

   BACK(0),FOLDER(1),FILE(2);

    private Integer value;

    FileTyp(Integer value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
