package com.wangky.video.task;

import android.os.AsyncTask;
import android.os.Environment;

import java.io.File;

public class DownloadTask extends AsyncTask<String,Void,String> {


    @Override
    protected String doInBackground(String... strings) {
        String filePath = strings[0];

        String DownloadDir = Environment.getExternalStorageDirectory().getPath();

        File file = new File(DownloadDir + File.separator + "MVideo");

        if(!file.exists()){
            file.mkdirs();
        }




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
