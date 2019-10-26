package com.wangky.video.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.wangky.video.MainActivity;
import com.wangky.video.R;
import com.wangky.video.util.DBHelper;
import com.wangky.video.util.PermissionHelper;

public class WelcomeActivity extends AppCompatActivity {

    private PermissionHelper mPermissionHelper;

    private DBHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

        mDbHelper = new DBHelper(this);

        //权限获取助手
        mPermissionHelper = new PermissionHelper(this);
        mPermissionHelper.setOnApplyPermissionListener(() -> nextOperation());

        if(Build.VERSION.SDK_INT < 23){ //android 6.0 以下
            nextOperation();
        }else if(mPermissionHelper.isAllRequestedPermissionGranted()){ //授权了所有的权限
            nextOperation();
        }else {
            //请求权限
            mPermissionHelper.applyPermissions();
        }

    }





    public void nextOperation(){
        new Handler().postDelayed(() -> {
            //延迟2秒后跳转到 MainActivity
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(intent);
        },1000);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //交给mPermissionHelper进行处理
        mPermissionHelper.onRequestPermissionsResult(requestCode,permissions,grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //交给mPermissionHelper 处理
        super.onActivityResult(requestCode, resultCode, data);
        mPermissionHelper.onActivityResult(requestCode, resultCode, data);
    }
}
