package com.wangky.video.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wangky.video.R;
import com.wangky.video.adapter.TorrentFileListAdapter;
import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.beans.TorrentInfoEntity;
import com.wangky.video.model.DownLoadModel;
import com.wangky.video.model.DownLoadModelImp;
import com.wangky.video.util.Const;
import com.wangky.video.util.FileUtils;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.TorrentFileInfo;
import com.xunlei.downloadlib.parameter.TorrentInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TorrentDetailActivity extends AppCompatActivity {

    private String DownloadDir ;

    private XLTaskHelper mTaskHelper;

    private RecyclerView mFileListView;

    private TorrentFileListAdapter mAdapter;

    private List<TorrentFileInfo> mList;

//    private String type;

    private String path;//种子文件所在地址

    private DownLoadModel downLoadModel;

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
          int position = holder.getAdapterPosition();
            TorrentFileInfo file = mList.get(position);

            DownloadTaskEntity task = downLoadModel.startTorrentTask(path,new int[]{file.mFileIndex});
            if(null != task && null !=task.getSubTasks()){
                List<TorrentInfoEntity> subs = task.getSubTasks();
                //找出当前对应的子任务
                TorrentInfoEntity target = null;
                for (TorrentInfoEntity entity:subs){
                    if(entity.getmFileIndex() == file.mFileIndex){
                        target = entity;
                        break;
                    }
                }

                if(null != target){
                    String path = target.getPath();
                    String localUrl = mTaskHelper.getLocalUrl(path);
                    Intent intent = new Intent(TorrentDetailActivity.this, PlayActivity.class);
                    intent.putExtra("taskId",task.getTaskId());//用来查询下载速度
                    intent.putExtra("hash",task.getHash());//用来查找DownloadTaskEntity
                    intent.putExtra("LOrientation",true);
                    intent.putExtra("data",localUrl);
                    intent.putExtra("title",target.getmFileName());
                    startActivity(intent);
                }else {
                    Toast.makeText(TorrentDetailActivity.this,"创建任务成功，请前往下载列表查看",Toast.LENGTH_LONG).show();
                }
            }

        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torrent_detail);
        setTitle("种子详情");
        //文件列表
        mFileListView = findViewById(R.id.file_list);
        mAdapter = new TorrentFileListAdapter(this,new ArrayList<>(),mListener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,RecyclerView.VERTICAL,false);
        mFileListView.setAdapter(mAdapter);
        mFileListView.setLayoutManager(layoutManager);
        mFileListView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        downLoadModel=new DownLoadModelImp();

        mTaskHelper= XLTaskHelper.instance(this);
        Intent intent = getIntent();
//         type = intent.getStringExtra("type");
         path = intent.getStringExtra("path");

         String fileFolder = FileUtils.getFileName(path);


        TorrentInfo info = mTaskHelper.getTorrentInfo(path);
        if(info.mFileCount == 0 || null ==info.mSubFileInfo){
            Toast.makeText(this,"获取文件信息失败",Toast.LENGTH_LONG).show();
            return;
        }

        fileFolder = info.mMultiFileBaseFolder;
        TorrentFileInfo[] fileArr = info.mSubFileInfo;
        mList = Arrays.asList(fileArr);
        mAdapter.addFiles(mList);


        DownloadDir = Const.File_SAVE_PATH +File.separator+fileFolder;

        File file = new File(DownloadDir);

        if(!file.exists()){
            file.mkdirs();
        }



    }
}
