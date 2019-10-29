package com.wangky.video.task;

import android.os.AsyncTask;
import android.os.SystemClock;

import com.wangky.video.util.DownUtil;

public class DownloadTask extends AsyncTask<Void,Void,Void> {

    @Override
    protected Void doInBackground(Void... voids) {

            while (DownUtil.getInstance().isIsLoopDown()){
                //更新ui
                System.out.println("---------更新UI--------");

                UpdateUI.getInstance().UpdateDownloadUI();


                SystemClock.sleep(1000);
            }

        return null;
    }
}
