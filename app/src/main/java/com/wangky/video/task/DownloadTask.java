package com.wangky.video.task;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.TorrentFileInfo;
import com.xunlei.downloadlib.parameter.TorrentInfo;

import java.io.File;

public class DownloadTask extends AsyncTask<String,Void,String> {

    private Context mContext;

    private XLTaskHelper mTaskHelper;

    public DownloadTask(Context context) {
        this.mContext = context;

        this.mTaskHelper = XLTaskHelper.instance(context);
    }




    @Override
    protected String doInBackground(String... strings) {
        String filePath = strings[0];
        String DownloadDir = Environment.getExternalStorageDirectory().getPath();
        File file = new File(DownloadDir + File.separator + "MVideo");
        if(!file.exists()){
            file.mkdirs();
        }

        TorrentInfo info = mTaskHelper.getTorrentInfo(filePath);
        TorrentFileInfo[] arr = info.mSubFileInfo;




        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);


    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);




    }
}
