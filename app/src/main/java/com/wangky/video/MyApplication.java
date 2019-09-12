package com.wangky.video;

import android.app.Application;
import android.content.Context;

import com.xunlei.downloadlib.XLTaskHelper;

public class MyApplication extends Application {

    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        XLTaskHelper.init(this);
    }

    public static Context getInstance() {
        return mContext;
    }


}
