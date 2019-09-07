package com.wangky.video.util;

import java.io.IOException;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpUtils {

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");


    private static OkHttpClient mHttpClient;

    public OkHttpUtils() {
        this.mHttpClient = new OkHttpClient();
    }


    public static OkHttpUtils getInstance(){

        return new OkHttpUtils();
    }


    public  String getQuery(String url) throws IOException {

        Request request = new Request.Builder().url(url).build();

        Response response = mHttpClient.newCall(request).execute();

        return response.body().string();

    }



    public  String postQuery(String url,String json) throws IOException {

        RequestBody body = RequestBody.create(json,JSON); //RequestBody.create(JSON,json);


        Request request = new Request.Builder().url(url).post(body).build();

        Response response = mHttpClient.newCall(request).execute();

        return  response.body().string();


    }




    public  String getQueryWithHeaders(String url) throws IOException {


//        Headers headers = new Headers.Builder().add().build();

        Request request = new Request.Builder().url(url).build();

        Response response = mHttpClient.newCall(request).execute();

        return response.body().string();

    }




}
