package com.wangky.video.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.wangky.video.R;
import com.wangky.video.adapter.DownloadListAdapter;
import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.beans.TorrentInfoEntity;
import com.wangky.video.model.DownLoadModel;
import com.wangky.video.model.DownLoadModelImp;
import com.wangky.video.services.DownloadService;
import com.wangky.video.util.Const;
import com.wangky.video.util.DownUtil;
import com.xunlei.downloadlib.XLTaskHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class DownloadActivity extends AppCompatActivity {

    private XLTaskHelper mTaskHelper;

    private RecyclerView downloadList;

    private DownloadListAdapter downloadListAdapter;

    private DownLoadModel downLoadModel;

    List<DownloadTaskEntity> tasks = new ArrayList<>();

    private UpDateUIReceiver receiver;
    private LocalBroadcastManager localBroadcastManager;


    private DownloadListAdapter.OnOperationBtnClick mClickListener = new DownloadListAdapter.OnOperationBtnClick() {

        @Override
        public void onDelete(DownloadTaskEntity task) {
            String[] items = {"同时删除本地文件"};
            boolean[] bool= {false};
            new AlertDialog.Builder(DownloadActivity.this)
                    .setTitle("确认要删除下载任务吗？")
                    .setMultiChoiceItems(items, bool, (dialog, which, isChecked) -> bool[0] = isChecked)
                    .setPositiveButton("确定", (dialog, which) -> {
                        boolean flag = bool[0];
                        if(flag){
                            downLoadModel.deleTask(task,true,true);
                        }else{
                            downLoadModel.deleTask(task,true,false);
                        }

                        dialog.dismiss();
                    })
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .create().show();
        }

        @Override
        public void onStart(DownloadTaskEntity task) {
            downLoadModel.startTask(task);
        }

        @Override
        public void onPause(DownloadTaskEntity task) {
            downLoadModel.stopTask(task);
        }

        @Override
        public void onDetail(DownloadTaskEntity task) {
            //未下载完成，开启任务
            if(Const.DOWNLOAD_STOP ==task.getmTaskStatus() || Const.DOWNLOAD_FAIL == task.getmTaskStatus()){
                downLoadModel.startTask(task);
            }

            List<TorrentInfoEntity> subs = task.getSubTasks();
            Intent intent = new Intent(DownloadActivity.this,SubTaskActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("subTasks", (Serializable) subs);
            intent.putExtras(bundle);
            startActivity(intent);

//          String path =   downLoadModel.getLocalUrl(task.getLocalPath()+ File.separator+task.getmFileName());
//          System.out.println("path---------->"+path);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        setTitle("下载列表");
        mTaskHelper= XLTaskHelper.instance(this);
        downLoadModel = new DownLoadModelImp();

        downloadList = findViewById(R.id.downloadList);

//        DBTools.getInstance().findALLTask();

        downloadListAdapter = new DownloadListAdapter(DownloadActivity.this,tasks,mClickListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(DownloadActivity.this);

        downloadList.setAdapter(downloadListAdapter);

        downloadList.setLayoutManager(layoutManager);


        receiver = new UpDateUIReceiver();
        IntentFilter intentFilter = new IntentFilter(Const.UPDATE_DOWNLOAD_UI);
        localBroadcastManager= LocalBroadcastManager.getInstance(this);
        localBroadcastManager.registerReceiver(receiver,intentFilter);

        //启动service更新UI
        Intent intent = new Intent(DownloadActivity.this, DownloadService.class);
        startService(intent);

    }



    public void refreshData(List<DownloadTaskEntity> data){
        tasks.clear();
        tasks.addAll(data);

        downloadListAdapter.notifyDataSetChanged();
    }



    class UpDateUIReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
             List<DownloadTaskEntity> tasks = (List<DownloadTaskEntity>) intent.getSerializableExtra("data");
             refreshData(tasks);
        }
    }


    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(receiver);
        //停止更新页面
        DownUtil.getInstance().setIsLoopDown(false);
        Intent intent = new Intent(DownloadActivity.this, DownloadService.class);
        stopService(intent);
        super.onDestroy();
    }

}
