package com.wangky.video.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wangky.video.R;
import com.wangky.video.adapter.DownloadListAdapter;
import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.beans.TorrentInfoEntity;
import com.wangky.video.dao.DBTools;
import com.wangky.video.enums.MessageType;
import com.wangky.video.event.TaskEvent;
import com.wangky.video.model.DownLoadModel;
import com.wangky.video.model.DownLoadModelImp;
import com.wangky.video.services.DownloadManageService;
import com.wangky.video.task.DownloadTasksManager;
import com.wangky.video.util.Const;
import com.wangky.video.util.DownUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DownloadActivity extends AppCompatActivity {

    private RecyclerView downloadList;

    private DownloadListAdapter downloadListAdapter;

    private DownLoadModel downLoadModel;

    List<DownloadTaskEntity> tasks = new ArrayList<>();

    private DownloadTasksManager downloadTasksManager;

    private DownloadManageService downloadManageService;


    private DownloadListAdapter.OnOperationBtnClick mClickListener = new DownloadListAdapter.OnOperationBtnClick() {

        @Override
        public void onDelete(DownloadTaskEntity task,int position) {
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
                        tasks.remove(position);
                        downloadListAdapter.notifyItemRemoved(position);
                        downloadTasksManager.removeTask(task);
                    })
                    .setNegativeButton("取消", (dialog, which) -> dialog.dismiss())
                    .create().show();
        }

        @Override
        public void onStart(DownloadTaskEntity task,int position) {
            downLoadModel.startTask(task);
           DownloadTaskEntity ta = tasks.get(position);
           ta.setmTaskStatus(Const.DOWNLOAD_CONNECTION);
            downloadListAdapter.notifyItemChanged(position);
            downloadTasksManager.addNewTask(task);
        }

        @Override
        public void onPause(DownloadTaskEntity task,int position) {
            downLoadModel.stopTask(task);
            DownloadTaskEntity ta = tasks.get(position);
            ta.setmTaskStatus(Const.DOWNLOAD_STOP);
            ta.setmDownloadSpeed(0);
            downloadListAdapter.notifyItemChanged(position);
            downloadTasksManager.removeTask(task);
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
            if(DownUtil.isDownSuccess(task)){
                bundle.putLong("taskId",-1);
            }else{
                bundle.putLong("taskId",task.getTaskId());
            }
            bundle.putString("hash",task.getHash());
            intent.putExtras(bundle);
            startActivity(intent);

//          String path =   downLoadModel.getLocalUrl(task.getLocalPath()+ File.separator+task.getmFileName());
//          System.out.println("path---------->"+path);

        }
    };


    private ServiceConnection connection =  new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DownloadManageService.DownloadManagerBinder binder = ((DownloadManageService.DownloadManagerBinder)service);
            downloadManageService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            downloadManageService = null;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        setTitle("下载列表");
        downLoadModel = new DownLoadModelImp();
        downloadList = findViewById(R.id.downloadList);

        tasks = DBTools.getInstance().findALLTask();

        downloadListAdapter = new DownloadListAdapter(DownloadActivity.this,tasks,mClickListener);
        LinearLayoutManager layoutManager = new LinearLayoutManager(DownloadActivity.this);
        downloadList.setAdapter(downloadListAdapter);
        downloadList.setLayoutManager(layoutManager);
        downloadTasksManager = DownloadTasksManager.getInstance();

        Intent intent = new Intent(DownloadActivity.this, DownloadManageService.class);
        startService(intent);

        bindService(intent,connection,BIND_AUTO_CREATE);

    }

    public void refreshData(List<DownloadTaskEntity> data){
        for(DownloadTaskEntity task : data){
            int i;
            for(i = 0 ; i < tasks.size(); i++){
                DownloadTaskEntity out = tasks.get(i);
                if(out.getHash().equalsIgnoreCase(task.getHash())){//对应任务
                    out.setmDownloadSpeed(task.getmDownloadSpeed());
                    out.setTaskId(task.getTaskId());
                    if(out.getmTaskStatus()!= task.getmTaskStatus()){
                        out.setmTaskStatus(task.getmTaskStatus());
                    }
                    out.setmDCDNSpeed(task.getmDCDNSpeed());
                    out.setmDownloadSpeed(task.getmDownloadSpeed());
                    out.setmDownloadSize(task.getmDownloadSize());
                    out.setmFileSize(task.getmFileSize());
                    downloadListAdapter.notifyItemChanged(i);
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /***
     * 更新下载进度
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateDownloadTaskUI(TaskEvent event){
        if(event.getMessage().equals(MessageType.UPDATE_UI)){
            List<DownloadTaskEntity> tasks = event.getTasks();
            refreshData(tasks);
        }
    }


}
