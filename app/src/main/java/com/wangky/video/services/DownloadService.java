package com.wangky.video.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.wangky.video.event.TaskEvent;
import com.wangky.video.task.DownloadTask;
import com.wangky.video.util.DownUtil;

import org.greenrobot.eventbus.EventBus;

public class DownloadService extends Service {
    public DownloadService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!DownUtil.getInstance().isIsLoopDown()){//当前是停止状态，需要启动
            DownUtil.getInstance().setIsLoopDown(true);
            TaskEvent event = new TaskEvent("更新下载进度");
            EventBus.getDefault().post(event);
            new DownloadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("DownloadService===>","onDestroy");
    }
}
