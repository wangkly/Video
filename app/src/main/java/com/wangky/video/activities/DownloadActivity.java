package com.wangky.video.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wangky.video.R;
import com.wangky.video.adapter.DownloadListAdapter;
import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.dao.DBTools;
import com.xunlei.downloadlib.XLTaskHelper;

import java.util.List;

public class DownloadActivity extends AppCompatActivity {

    private XLTaskHelper mTaskHelper;

    private RecyclerView downloadList;

    private DownloadListAdapter downloadListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        setTitle("下载列表");
        mTaskHelper= XLTaskHelper.instance(this);

        downloadList = findViewById(R.id.downloadList);

        List<DownloadTaskEntity> tasks = DBTools.getInstance().findALLTask();

        downloadListAdapter = new DownloadListAdapter(DownloadActivity.this,tasks);

        LinearLayoutManager layoutManager = new LinearLayoutManager(DownloadActivity.this);

        downloadList.setAdapter(downloadListAdapter);

        downloadList.setLayoutManager(layoutManager);


    }
}
