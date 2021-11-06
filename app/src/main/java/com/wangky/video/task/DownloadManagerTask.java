package com.wangky.video.task;

import android.os.AsyncTask;
import android.os.SystemClock;
import android.util.Log;

import com.wangky.video.util.DownUtil;

public class DownloadManagerTask extends AsyncTask<Void,Void,Void> {
    private final String TAG = "DownloadManagerTask";

    @Override
    protected Void doInBackground(Void... params) {
        //后台任务，不断的更新task下载进度
        while (DownUtil.getInstance().isIsLoopDown()){//用于判断是否要停止更新的flag
            //查询task,如果需要更新，则不断的查询迅雷的接口更新下载进度、速度
            //更新ui
            Log.i(TAG,"---------更新UI--------");
            UpdateManagerUI.getInstance().UpdateDownloadUI();
            SystemClock.sleep(1000);
        }

        return null;
    }





}
