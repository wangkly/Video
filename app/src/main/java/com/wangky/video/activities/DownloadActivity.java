package com.wangky.video.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.wangky.video.R;
import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.dao.DBTools;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

import java.util.List;

public class DownloadActivity extends AppCompatActivity {

    private XLTaskHelper mTaskHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        setTitle("下载列表");

        mTaskHelper= XLTaskHelper.instance(this);

        List<DownloadTaskEntity> tasks = DBTools.getInstance().findALLTask();

        for (DownloadTaskEntity entity :tasks){

            XLTaskInfo info = mTaskHelper.getTaskInfo(entity.getTaskId());
            System.out.println(info.mDownloadSpeed);
        }



    }
}
