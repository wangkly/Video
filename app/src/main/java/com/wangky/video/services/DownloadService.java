package com.wangky.video.services;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.wangky.video.task.DownloadTask;
import com.wangky.video.util.DownUtil;

public class DownloadService extends Service {

    private DownloadBinder binder  = new DownloadBinder();


    @Override
    public IBinder onBind(Intent intent) {
        return  binder;
    }

    /**ø
     * 多次startService,onStartCommand会执行多次
     * 判断DownUtil.getInstance().isIsLoopDown() 是否已经在执行，
     * 如果已经在执行,则跳过
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!DownUtil.getInstance().isIsLoopDown()){//当前是停止状态，需要启动
            DownUtil.getInstance().setIsLoopDown(true);
            new DownloadTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("DownloadService===>","onDestroy");
    }


    private class DownloadBinder extends Binder{

       public DownloadService  getService(){
           return DownloadService.this;
       }

    }

}
