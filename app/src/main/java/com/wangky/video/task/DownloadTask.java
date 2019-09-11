package com.wangky.video.task;

import android.os.AsyncTask;
import android.os.Environment;

import com.turn.ttorrent.client.SimpleClient;

import java.io.File;

public class DownloadTask extends AsyncTask<String,Void,String> {


    @Override
    protected String doInBackground(String... strings) {
        String filePath = strings[0];
        SimpleClient client = new SimpleClient();

        String DownloadDir = Environment.getExternalStorageDirectory().getPath();

        File file = new File(DownloadDir + File.separator + "MVideo");

        if(!file.exists()){
            file.mkdirs();
        }

//        try {
//            InetAddress address = InetAddress.getLocalHost();
//            client.downloadTorrent(filePath,
//                    file.getAbsolutePath(),
//                    address);
//            //download finished
//        } catch (Exception e) {
//            //download failed, see exception for details
//            e.printStackTrace();
//        }


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
