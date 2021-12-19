package com.wangky.video;

import android.app.Application;
import android.content.pm.PackageManager;
import android.util.Log;

import com.facebook.react.ReactApplication;
import com.facebook.react.ReactNativeHost;
import com.facebook.react.ReactPackage;
import com.facebook.react.shell.MainReactPackage;
import com.facebook.soloader.SoLoader;
import com.wangky.video.listeners.MyExceptionHandler;
import com.wangky.video.rnmodule.RnPackage;
import com.xunlei.downloadlib.XLTaskHelper;

import java.util.ArrayList;
import java.util.List;

public class MyApplication extends Application implements ReactApplication {

    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        XLTaskHelper.init(this);
        Thread.setDefaultUncaughtExceptionHandler(MyExceptionHandler.getInstance());
        SoLoader.init(this, false);
        reactNativeHost.getReactInstanceManager().createReactContextInBackground();
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



    private ReactNativeHost reactNativeHost = new ReactNativeHost(this) {
        @Override
        public boolean getUseDeveloperSupport() {
            return BuildConfig.DEBUG;
        }

        @Override
        protected List<ReactPackage> getPackages() {
            List<ReactPackage> packages = new ArrayList<>();
            packages.add(new RnPackage());
            packages.add(new MainReactPackage());
            return packages;
        }

        /**
         * 自定义bundle 文件地址
         *
        @Nullable
        @Override
        protected String getJSBundleFile() {
            String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "rn/index.android1.bundle";
            File file = new File(path);
            if(file !=null && file.exists()){
                return path;
            }
            return super.getJSBundleFile();
        }
      */
    };


    @Override
    public ReactNativeHost getReactNativeHost() {
        return reactNativeHost;
    }


}
