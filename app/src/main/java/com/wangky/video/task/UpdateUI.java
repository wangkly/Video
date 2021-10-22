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
import com.wangky.video.util.DownUtil;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateUI {

    private static UpdateUI updateUI;
    private  static List<DownloadTaskEntity> needsRestarts = new ArrayList<>();

    List<DownloadTaskEntity> tasks;

    private DownLoadModel downLoadModel;

    private UpdateUI() {
        this.tasks = new ArrayList<>();

        downLoadModel = new DownLoadModelImp();
    }

    public static synchronized UpdateUI getInstance() {
        if (updateUI == null) {
            updateUI = new UpdateUI();
            initRestartTimer();
        }
        try{
           List<DownloadTaskEntity> all = DBTools.getInstance().findALLTask();
            updateUI.tasks = all;
        }catch (Exception e){
            e.printStackTrace();
            Log.e("UpdateUI",e.getMessage());
        }
        return updateUI;
    }



    public void UpdateDownloadUI(){
        //获取正在下载中的任务
        List<DownloadTaskEntity> subTasks = new ArrayList<>();
        for(DownloadTaskEntity task:tasks){
            if(task.getmTaskStatus() == Const.DOWNLOAD_LOADING){
                subTasks.add(task);
            }
        }

        if(subTasks.size() > 0){
            for(DownloadTaskEntity task : subTasks){
                //非暂停或等待的任务，------>进行中的任务/成功的任务
                if (task.getmTaskStatus() != Const.DOWNLOAD_STOP && task.getmTaskStatus() != Const.DOWNLOAD_WAIT  && task.getTaskId()!=0) {
                    //获取当前任务状态
                    XLTaskInfo taskInfo = XLTaskHelper.instance(MyApplication.getInstance()).getTaskInfo(task.getTaskId());
                    task.setTaskId(taskInfo.mTaskId);
                    task.setmTaskStatus(taskInfo.mTaskStatus);
                    task.setmDCDNSpeed(taskInfo.mAdditionalResDCDNSpeed);
                    task.setmDownloadSpeed(taskInfo.mDownloadSpeed);
                    if (taskInfo.mTaskId != 0) {
                        task.setmFileSize(taskInfo.mFileSize);
                        task.setmDownloadSize(taskInfo.mDownloadSize);
                    }

                    //已下载成功了
                    if (DownUtil.isDownSuccess(task)) {
                        downLoadModel.stopTask(task);
                        task.setmTaskStatus(Const.DOWNLOAD_SUCCESS);
                        DBTools.getInstance().updateMainTask(task);//更新数据
                    }

                    if(task.getmTaskStatus() == Const.DOWNLOAD_CONNECTION ){//没有下载速度了，需要重启
                        boolean alreadyExist = false;
                        for (DownloadTaskEntity en:needsRestarts){
                            if(en.getHash().equalsIgnoreCase(task.getHash())){
                                alreadyExist = true;
                            }
                        }
                        if(!alreadyExist){
                            needsRestarts.add(task);
                        }
                    }
                }
            }

            //只更新这些正在下载中的任务下载进度
            TaskEvent event =  new TaskEvent(MessageType.UPDATE_UI,subTasks);
            EventBus.getDefault().post(event);
        }
    }


    public static void  initRestartTimer(){
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //如果有需要重启的任务
                if(needsRestarts.size() > 0){
                   ArrayList<DownloadTaskEntity> newArr =  new ArrayList<>(needsRestarts);
                    new Thread(new RestartTask(newArr)).start();
                }
                //清空
                needsRestarts.clear();
            }
        };
        timer.schedule(timerTask,1000,60000);
    }

    public void  stopTask(){
        //空的下载列表应该停止更新
        TaskEvent event =  new TaskEvent(MessageType.STOP_TASK);
        EventBus.getDefault().post(event);
        DownUtil.getInstance().setIsLoopDown(false);
    }


}
