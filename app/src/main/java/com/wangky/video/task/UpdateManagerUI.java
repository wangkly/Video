package com.wangky.video.task;

import android.util.Log;

import com.wangky.video.MyApplication;
import com.wangky.video.beans.DownloadTaskEntity;
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

public class UpdateManagerUI {

    private  final String TAG = "UpdateManagerUI.class";

    private static UpdateManagerUI updateUI;

    private DownLoadModel downLoadModel;

    private UpdateManagerUI() {
        downLoadModel = new DownLoadModelImp();
    }

    public static synchronized UpdateManagerUI getInstance() {
        if (updateUI == null) {
            updateUI = new UpdateManagerUI();
        }
        return updateUI;
    }



    public void UpdateDownloadUI(){
        //获取正在下载中的任务,单例模式，获取的是同一个对象
        List<DownloadTaskEntity> subTasks =  DownloadTasksManager.getInstance().getDownloadingList();

        List<DownloadTaskEntity> needsRestarts = new ArrayList<>();
        List<DownloadTaskEntity> needsDel = new ArrayList<>();

        if(subTasks.size() > 0){
            for(DownloadTaskEntity task : subTasks){
                //非暂停或等待的任务，------>进行中的任务/成功的任务
                if (task.getmTaskStatus() != Const.DOWNLOAD_STOP && task.getmTaskStatus() != Const.DOWNLOAD_WAIT) {
                    //获取当前任务状态
                    XLTaskInfo taskInfo = XLTaskHelper.instance(MyApplication.getInstance()).getTaskInfo(task.getTaskId());
                    task.setTaskId(taskInfo.mTaskId);
                    task.setmTaskStatus(taskInfo.mTaskStatus);
                    task.setmDCDNSpeed(taskInfo.mAdditionalResDCDNSpeed);
                    task.setmDownloadSpeed(taskInfo.mDownloadSpeed);
                    Log.i(TAG,"taskId-"+taskInfo.mTaskId+"-speed--->"+taskInfo.mDownloadSpeed);
                    if (taskInfo.mTaskId != 0) {
                        task.setmFileSize(taskInfo.mFileSize);
                        task.setmDownloadSize(taskInfo.mDownloadSize);
                    }

                    //已下载成功了
                    if (DownUtil.isDownSuccess(task)) {
                        downLoadModel.stopTask(task);
                        task.setmTaskStatus(Const.DOWNLOAD_SUCCESS);

                        boolean alreadyExist = false;
                        for (DownloadTaskEntity en:needsDel){
                            if(en.getHash().equalsIgnoreCase(task.getHash())){
                                alreadyExist = true;
                            }
                        }
                        if(!alreadyExist){
                            needsDel.add(task);
                        }
                    }

                    if(task.getmTaskStatus() == Const.DOWNLOAD_CONNECTION ||
                            (task.getmTaskStatus() == Const.DOWNLOAD_LOADING && task.getmDownloadSpeed() == 0 )||//没有下载速度了，需要重启
                            task.getTaskId() ==0 ){
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

            if(needsDel.size() > 0){
                //发通知，把这个任务从更新的列表中移除，不用再更新
                Log.i(TAG,"有"+needsDel.size()+"个任务已完成");
                TaskEvent delEvent =  new TaskEvent(MessageType.DEL_TASK, needsDel);
                EventBus.getDefault().post(delEvent);
            }

            if(needsRestarts.size() > 0){
                Log.i(TAG,"有"+needsRestarts.size()+"个任务需重启");
                TaskEvent restart =  new TaskEvent(MessageType.RESTART_TASK, needsRestarts);
                EventBus.getDefault().post(restart);
            }

            //只更新这些正在下载中的任务下载进度
            TaskEvent event =  new TaskEvent(MessageType.UPDATE_UI,subTasks);
            EventBus.getDefault().post(event);
        }
    }


//    public static void  initRestartTimer(){
//        Timer timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                //如果有需要重启的任务
//                if(needsRestarts.size() > 0){
//                Log.i("initRestartTimer","重启任务开始-->"+System.currentTimeMillis());
//                   ArrayList<DownloadTaskEntity> newArr =  new ArrayList<>(needsRestarts);
//                    new Thread(new RestartTask(newArr)).start();
//                    //清空
//                    needsRestarts.clear();
//                }
//            }
//        };
//        timer.schedule(timerTask,1000,30000);
//    }


}
