package com.wangky.video.task;

import android.util.Log;

import com.wangky.video.MyApplication;
import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.dao.DBTools;
import com.wangky.video.model.DownLoadModel;
import com.wangky.video.model.DownLoadModelImp;
import com.wangky.video.util.Const;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

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
            System.out.println("---------重启任务--------");
            //正在下载任务
            for(DownloadTaskEntity task : tasks){
                DownloadTaskEntity dbTask = DBTools.getInstance().findByHash(task.getHash());
                if(dbTask.getmTaskStatus() == Const.DOWNLOAD_LOADING ){
                    //获取当前任务状态
                    XLTaskInfo taskInfo = XLTaskHelper.instance(MyApplication.getInstance()).getTaskInfo(task.getTaskId());
                    if(taskInfo.mTaskStatus == Const.DOWNLOAD_CONNECTION ){
                        this.model.restartDownloadTask(task.getHash());
                    }

                }

                System.out.println("---------重启任务--------"+task.getHash());
                Log.i("--Restart---->",task.getHash());
            }
    }
}
