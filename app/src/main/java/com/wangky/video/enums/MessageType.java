package com.wangky.video.enums;

public enum MessageType {

    UPDATE_UI(0,"更新下载任务"),
    STOP_TASK(1,"停止任务"),
    DEL_TASK(2,"删除任务"),
    RESTART_UPDATE_UI(3,"重启更新列表"),
    RESTARTED(4,"任务重启");

    private Integer value;

    private String desc;

    MessageType(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }


    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
