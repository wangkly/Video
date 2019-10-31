package com.wangky.video.task;

import android.content.Intent;
import android.os.Bundle;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.wangky.video.MyApplication;
import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.dao.DBTools;
import com.wangky.video.model.DownLoadModel;
import com.wangky.video.model.DownLoadModelImp;
import com.wangky.video.util.Const;
import com.wangky.video.util.DownUtil;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UpdateUI {

    private static UpdateUI updateUI;

    List<DownloadTaskEntity> tasks;

    private DownLoadModel downLoadModel;

    public UpdateUI() {
        this.tasks = new ArrayList<>();

        downLoadModel = new DownLoadModelImp();
    }

    public static synchronized UpdateUI getInstance() {
        if (updateUI == null) {
            updateUI = new UpdateUI();
        }
        return updateUI;
    }



    public void UpdateDownloadUI(){
        tasks = DBTools.getInstance().findALLTask();
        if(tasks.size() > 0){
            for(DownloadTaskEntity task : tasks){
                //非暂停或等待的任务，------>进行中的任务/成功的任务
                if (task.getmTaskStatus() != Const.DOWNLOAD_STOP && task.getmTaskStatus() != Const.DOWNLOAD_WAIT  && task.getTaskId()!=0) {
                    XLTaskInfo taskInfo = XLTaskHelper.instance(MyApplication.getInstance()).getTaskInfo(task.getTaskId());
                    task.setTaskId(taskInfo.mTaskId);
                    task.setmTaskStatus(taskInfo.mTaskStatus);
                    task.setmDCDNSpeed(taskInfo.mAdditionalResDCDNSpeed);
                    task.setmDownloadSpeed(taskInfo.mDownloadSpeed);
                    if (taskInfo.mTaskId != 0) {
                        task.setmFileSize(taskInfo.mFileSize);
                        task.setmDownloadSize(taskInfo.mDownloadSize);
                    }

//                    DBTools.getInstance().saveOrUpdate(task);
                    //已下载成功了
                    if (DownUtil.isDownSuccess(task)) {
                        downLoadModel.stopTask(task);
                        task.setmTaskStatus(Const.DOWNLOAD_SUCCESS);
                    }
                    DBTools.getInstance().updateMainTask(task);
                }

            }

            //通知activity 更新
            Intent intent = new Intent(Const.UPDATE_DOWNLOAD_UI);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", (Serializable) tasks);
            intent.putExtras(bundle);

            LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);

        }else{
            //通知activity 更新
            Intent intent = new Intent(Const.UPDATE_DOWNLOAD_UI);
            Bundle bundle = new Bundle();
            bundle.putSerializable("data", new ArrayList<>());
            intent.putExtras(bundle);
            LocalBroadcastManager.getInstance(MyApplication.getInstance()).sendBroadcast(intent);

        }
    }


}
