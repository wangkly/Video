package com.wangky.video.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.wangky.video.R;
import com.wangky.video.adapter.TorrentFileListAdapter;
import com.wangky.video.model.DownLoadModel;
import com.wangky.video.model.DownLoadModelImp;
import com.wangky.video.util.Const;
import com.wangky.video.util.FileUtils;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.TorrentFileInfo;
import com.xunlei.downloadlib.parameter.TorrentInfo;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TorrentDetailActivity extends AppCompatActivity {

    private String DownloadDir ;

    private XLTaskHelper mTaskHelper;

    private RecyclerView mFileListView;

    private TorrentFileListAdapter mAdapter;

    private List<TorrentFileInfo> mList;

    private String type;

    private String path;//种子文件所在地址

    private DownLoadModel downLoadModel;

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
          int position = holder.getAdapterPosition();
            TorrentFileInfo file = mList.get(position);
            long taskId = 0;
//            try {
//                taskId = mTaskHelper.addTorrentTask(path,DownloadDir,new int[]{file.mFileIndex});
////                    String mPlayUrl = mTaskHelper.getLocalUrl(DownloadDir+File.separator+file.mFileName);
//                XLTaskInfo task = mTaskHelper.getTaskInfo(taskId);
//                System.out.println(task.mDownloadSpeed);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
            downLoadModel.startTorrentTask(path,new int[]{file.mFileIndex});

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
         type = intent.getStringExtra("type");
         path = intent.getStringExtra("path");

         String fileFolder = FileUtils.getFileName(path);

        //选取种子文件
        if(type.equalsIgnoreCase("torrent")){
            TorrentInfo info = mTaskHelper.getTorrentInfo(path);
            fileFolder = info.mMultiFileBaseFolder;

            TorrentFileInfo[] fileArr = info.mSubFileInfo;
            mList = Arrays.asList(fileArr);
            mAdapter.addFiles(mList);

        }else if(type.equalsIgnoreCase("url")) {



        }


        DownloadDir = Const.File_SAVE_PATH +File.separator+fileFolder;

        File file = new File(DownloadDir);

        if(!file.exists()){
            file.mkdirs();
        }



    }
}
