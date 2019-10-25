package com.wangky.video;

import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import com.xunlei.downloadlib.XLTaskHelper;

public class MyApplication extends Application {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        XLTaskHelper.init(this);
    }

    public static MyApplication getInstance() {
        return instance;
    }



    @Override
    public String getPackageName() {
        if(Log.getStackTraceString(new Throwable()).contains("com.xunlei.downloadlib")) {
            return "com.xunlei.downloadprovider";
        }
        return super.getPackageName();
    }
    @Override
    public PackageManager getPackageManager() {
        if(Log.getStackTraceString(new Throwable()).contains("com.xunlei.downloadlib")) {
            return new DelegateApplicationPackageManager(super.getPackageManager());
        }
        return super.getPackageManager();
    }

}
