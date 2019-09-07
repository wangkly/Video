package com.wangky.video.task;

import android.os.AsyncTask;

import com.wangky.video.listeners.HttpRequestListener;
import com.wangky.video.util.OkHttpUtils;

import java.io.IOException;


public class HttpTask extends AsyncTask<String,Void,String> {


    private HttpRequestListener listener;


    public HttpTask(HttpRequestListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... strings) {

        String url = strings[0];

        try {
            String resp =  OkHttpUtils.getInstance().getQuery(url);

            return  resp;

        } catch (IOException e) {
            e.printStackTrace();
            listener.OnFailed("请求出错了");
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
        listener.onSuccess(s);
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
}
