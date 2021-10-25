package com.wangky.video.task;

import android.util.Log;

import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.dao.DBTools;
import com.wangky.video.util.Const;

import java.util.ArrayList;
import java.util.List;

/**
 * 这个类只管有哪些下载项，提供getter
 * 添加、删除时判断是否需要发通知，启动或者暂停更新UI的Task
 */
public class DownloadTasksManager {

    private static DownloadTasksManager downloadTasksManager;
    private  List<DownloadTaskEntity> downloadingList;
    /**
     * 通知下载列表中有数据或者空的时候启动，暂停更新
     */
    private DownloadListListener downloadTasksChangeListener ;

    public List<DownloadTaskEntity> getDownloadingList() {
        return downloadingList;
    }

    public void setDownloadTasksChangeListener(DownloadListListener downloadTasksChangeListener) {
        this.downloadTasksChangeListener = downloadTasksChangeListener;
    }


    private DownloadTasksManager() {
        List<DownloadTaskEntity> all = new ArrayList<>();
        try {
            all = DBTools.getInstance().findALLTask();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("DownloadTasksManager",e.getMessage());
        }

        List<DownloadTaskEntity> subTasks = new ArrayList<>();
        //正在下载中的任务
        for(DownloadTaskEntity task:all){
            if(task.getmTaskStatus() == Const.DOWNLOAD_LOADING){
                subTasks.add(task);
            }
        }
        //初始化的时候查下db获取正在下载的tasksList
        this.downloadingList = subTasks;
    }

    /**
     * 做成单例的，这样所有地方获取的对象都是同一个，不同在方法里传递这个对象
     * @return
     */
    public synchronized static DownloadTasksManager getInstance(){
        if(downloadTasksManager == null){
            downloadTasksManager = new DownloadTasksManager();
        }
        return downloadTasksManager;
    }


    //往队列中添加新的任务，要启动更新下载列表的task
    public void  addNewTask(DownloadTaskEntity task){
        synchronized (DownloadTasksManager.class){
            boolean alreadyHas = false;
            for(DownloadTaskEntity entity : this.downloadingList){
                if(entity.getHash().equalsIgnoreCase(task.getHash())){
                    alreadyHas = true;
                }
            }
            if(!alreadyHas){
                this.downloadingList.add(task);
            }
            this.downloadTasksChangeListener.onNewTaskAdded();
        }
    }

    /**
     * 移除下载列表中的任务，下载列表中暂停了任务，要停止更新相应的taskUI
     * @param task
     */
    public void removeTask(DownloadTaskEntity task){
        synchronized (DownloadTasksManager.class){
            int targetIndex = -1;
            for (int index = 0; index < this.downloadingList.size();index++){
                DownloadTaskEntity entity = this.downloadingList.get(index);
                if(entity.getHash().equalsIgnoreCase(task.getHash())){
                    targetIndex = index;
                    break;
                }
            }
            if(targetIndex > -1){
                this.downloadingList.remove(targetIndex);
                this.downloadTasksChangeListener.onTaskRemoved();
            }
        }
    }

    /**
     * 重启，需更新对应任务的taskId
     * @param task
     */
    public synchronized void updateTargetTask(DownloadTaskEntity task){
        for (DownloadTaskEntity entity:this.downloadingList){
            if(entity.getHash().equalsIgnoreCase(task.getHash())){
                Log.i("downloadTaskManager","update TaskId "+task.getTaskId());
                Log.i("downloadTaskManager","hash====> "+task.getHash());
                entity.setTaskId(task.getTaskId());
                entity.setmTaskStatus(task.getmTaskStatus());
                entity.setmDownloadSpeed(task.getmDownloadSpeed());
            }
        }

    }


    public  interface DownloadListListener{
        void onNewTaskAdded();
        void onTaskRemoved();

    }

}


