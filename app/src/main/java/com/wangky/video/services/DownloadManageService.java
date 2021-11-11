package com.wangky.video.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.wangky.video.R;
import com.wangky.video.activities.DownloadActivity;
import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.enums.MessageType;
import com.wangky.video.event.TaskEvent;
import com.wangky.video.task.DownloadManagerTask;
import com.wangky.video.task.DownloadTasksManager;
import com.wangky.video.task.RestartTask;
import com.wangky.video.util.DownUtil;
import com.wangky.video.util.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DownloadManageService extends Service {

    private final String TAG = "DownloadManageService";
    private String channelId = "my_channel_01";

    private DownloadTasksManager tasksManager;

    private  List<DownloadTaskEntity> needsRestarts;
    public synchronized List<DownloadTaskEntity> getNeedsRestarts() {
        return needsRestarts;
    }

    public synchronized void setNeedsRestarts(List<DownloadTaskEntity> needsRestarts) {
        this.needsRestarts = needsRestarts;
    }


    private  Timer timer = new Timer();

    private NotificationManager nManager;

    private DownloadManagerBinder binder = new DownloadManagerBinder();

    private DownloadTasksManager.DownloadListListener downloadListListener = new DownloadTasksManager.DownloadListListener() {
        @Override
        public void onNewTaskAdded() {
            if(!DownUtil.getInstance().isIsLoopDown()){//如果添加新任务时，flag=false,需要重启更新服务
                Log.i(TAG,"有新任务添加，需要重新启动更新");
                TaskEvent event =  new TaskEvent(MessageType.RESTART_UPDATE_UI);
                EventBus.getDefault().post(event);
            }
        }

        @Override
        public void onTaskRemoved() {
            if(tasksManager.getDownloadingList().size() == 0){
                Log.i(TAG,"下载列表为空，更新任务暂停");
                DownUtil.getInstance().setIsLoopDown(false);// 设置后AsyncTask中会暂停
            }
        }
    };

    public DownloadManageService() {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate() {
        super.onCreate();
        nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = new NotificationChannel(channelId, "my_channel", NotificationManager.IMPORTANCE_LOW);
        nManager.createNotificationChannel(mChannel);
        this.tasksManager = DownloadTasksManager.getInstance();
        this.tasksManager.setDownloadTasksChangeListener(downloadListListener);
        this.initRestartTimer();
        EventBus.getDefault().register(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!DownUtil.getInstance().isIsLoopDown()){
            this.onStartUpdateUI();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        nManager.cancel(1);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  binder;
    }


    public class DownloadManagerBinder extends Binder{
       public DownloadManageService getService(){
            return DownloadManageService.this;
        }
    }



    public void  onStartUpdateUI(){
        // 内部更新的逻辑肯定要在子线程中执行，
        new DownloadManagerTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    /**
     * 定时重启任务
     */
    public  void  initRestartTimer(){
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                //如果有需要重启的任务
                if(needsRestarts.size() > 0){
                Log.i("initRestartTimer","重启任务开始-->"+System.currentTimeMillis());
                    ArrayList<DownloadTaskEntity> newArr = new ArrayList<>(getNeedsRestarts());
                    new Thread(new RestartTask(newArr)).start();
                    //清空
                    needsRestarts.clear();
                }
            }
        };
        timer.schedule(timerTask,1000,10000);
    }




    /**
     * 已完成的任务，把它从更新列表中移除
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void removeTasks(TaskEvent event){
        if(event.getMessage().equals(MessageType.DEL_TASK)){
            List<DownloadTaskEntity> tasks = event.getTasks();
            for(DownloadTaskEntity entity:tasks){
                this.tasksManager.removeTask(entity);
            }
        }
    }

    /**
     * 新添加任务，加入更新列表
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void AddTask(TaskEvent event){
        if(event.getMessage().equals(MessageType.ADD_TASK)){
            List<DownloadTaskEntity> tasks = event.getTasks();
            this.tasksManager.addNewTask(tasks.get(0));
        }
    }

    /**
     * 当所有任务暂停后，重新添加了一个任务，这时需要重启更新的task
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void needReStartUpdateUI(TaskEvent event) {
        if(event.getMessage().equals(MessageType.RESTART_UPDATE_UI)){
            DownUtil.getInstance().setIsLoopDown(true);
            this.onStartUpdateUI();
        }
    }

    /**
     * 重启后更新taskId之类信息
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void updateReStartTaskId(TaskEvent event){
        if(event.getMessage().equals(MessageType.RESTARTED)){
            List<DownloadTaskEntity> tasks = event.getTasks();
            for(DownloadTaskEntity entity:tasks){
                this.tasksManager.updateTargetTask(entity);
            }
        }
    }

    /**
     *重启需要更新的任务
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void restartTask(TaskEvent event){
        if(event.getMessage().equals(MessageType.RESTART_TASK)){
            List<DownloadTaskEntity> tasks = event.getTasks();
            this.setNeedsRestarts(tasks);
        }
    }


    /**
     * Notification在android 8.0以上设置时，需要设置渠道信息才能够正常显示通知
     * @param event
     */
    @TargetApi(Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMessage(TaskEvent event){
        MessageType message = event.getMessage();
        if(event.getMessage().equals(MessageType.UPDATE_UI)){

            List<DownloadTaskEntity> tasks = event.getTasks();
            NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
            for(DownloadTaskEntity task :tasks){
                String notStr = "";
                notStr += task.getmFileName();
                notStr += " - " + FileUtils.downloadSpeed(task.getmDownloadSpeed());
                inboxStyle.addLine(notStr);
            }

            Intent intent = new Intent(this, DownloadActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
            //setStyle 貌似在android6.0不起作用？？？
            Notification notification = new NotificationCompat
                    .Builder(getApplicationContext(),channelId)
                    .setContentTitle("正在下载")
                    .setContentText(message.name())
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_download)
                    .setWhen(System.currentTimeMillis())
                    .setStyle(inboxStyle)
    //                .setStyle(new NotificationCompat.BigTextStyle().bigText(notStr))
    //                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(),R.drawable.art_login_bg)))
                    .setAutoCancel(true)
    //                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .build();

    //        nManager.notify(1,notification);
            startForeground(1,notification);

        }
    }
}
