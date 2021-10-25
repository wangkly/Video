package com.wangky.video.task;

import android.util.Log;

import com.wangky.video.MyApplication;
import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.dao.DBTools;
import com.wangky.video.enums.MessageType;
import com.wangky.video.event.TaskEvent;
import com.wangky.video.model.DownLoadModel;
import com.wangky.video.model.DownLoadModelImp;
import com.wangky.video.util.Const;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

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

//                DownloadTaskEntity dbTask = DBTools.getInstance().findByHash(task.getHash());
//                if(dbTask.getmTaskStatus() == Const.DOWNLOAD_LOADING ){
//                    //获取当前任务状态
//                    XLTaskInfo taskInfo = XLTaskHelper.instance(MyApplication.getInstance()).getTaskInfo(task.getTaskId());
//                    if(taskInfo.mTaskStatus == Const.DOWNLOAD_CONNECTION ){
//                        this.model.restartDownloadTask(task.getHash());
//                    }
//
//                }
            }
    }
}
