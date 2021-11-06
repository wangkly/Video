package com.wangky.video.task;

import android.util.Log;

import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.enums.MessageType;
import com.wangky.video.event.TaskEvent;
import com.wangky.video.model.DownLoadModel;
import com.wangky.video.model.DownLoadModelImp;

import org.greenrobot.eventbus.EventBus;

import java.util.Collections;
import java.util.List;



/**
 * 不断重启，当前活跃的任务
 *
 */
public class RestartTask implements Runnable{

    List<DownloadTaskEntity> tasks;
    private DownLoadModel model;

    public RestartTask(List<DownloadTaskEntity> tasks) {
        this.model = new DownLoadModelImp();
        this.tasks = tasks;
    }


    @Override
    public void run() {
            //正在下载任务
            for(DownloadTaskEntity task : tasks){
                Log.i("RestartTask","重启任务--->"+task.getmFileName());
                Log.i("--Restart---->",task.getHash());
                DownloadTaskEntity reTask =  this.model.restartDownloadTask(task.getHash());
                //任务已重启，更新列表对应的任务需更新taskId
                TaskEvent event =  new TaskEvent(MessageType.RESTARTED, Collections.singletonList(reTask));
                EventBus.getDefault().post(event);

            }
    }
}
