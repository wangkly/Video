package com.wangky.video.event;

public class TaskEvent {

    private String message;


    public TaskEvent(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
