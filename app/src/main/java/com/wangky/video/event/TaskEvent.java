package com.wangky.video.event;

import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.enums.MessageType;

import java.util.List;

public class TaskEvent {

    private MessageType message;

    private List<DownloadTaskEntity> tasks;

    public TaskEvent(MessageType message) {
        this.message = message;
    }


    public TaskEvent(MessageType message, List<DownloadTaskEntity> tasks) {
        this.message = message;
        this.tasks = tasks;
    }

    public MessageType getMessage() {
        return message;
    }


    public void setMessage(MessageType message) {
        this.message = message;
    }


    public List<DownloadTaskEntity> getTasks() {
        return tasks;
    }

    public void setTasks(List<DownloadTaskEntity> tasks) {
        this.tasks = tasks;
    }
}
