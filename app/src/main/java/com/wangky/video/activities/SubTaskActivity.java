package com.wangky.video.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.wangky.video.R;
import com.wangky.video.adapter.TorrentTaskListAdapter;
import com.wangky.video.beans.TorrentInfoEntity;
import com.xunlei.downloadlib.XLTaskHelper;

import java.io.File;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubTaskActivity extends AppCompatActivity {

    private RecyclerView mSubList;
    private TorrentTaskListAdapter adapter;

    private List<TorrentInfoEntity> mList;

    private XLTaskHelper mTaskHelper;

    private long mTaskId = 0;
    private String hash;
    private String torrentPath;


    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            int position = holder.getAdapterPosition();
            TorrentInfoEntity file = mList.get(position);
            Intent intent = new Intent(SubTaskActivity.this, PlayActivity.class);
            String localUrl;
            if(mTaskId == -1){
                localUrl = file.getPath();
                intent.putExtra("taskId",0);
            }else{
                localUrl= mTaskHelper.getLocalUrl(file.getPath());
                intent.putExtra("taskId",mTaskId);
            }
            intent.putExtra("LOrientation",true);
            intent.putExtra("data",localUrl);
            intent.putExtra("title",file.getmFileName());
            intent.putExtra("hash",hash);
            startActivity(intent);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_task_wrap);
        setTitle("任务详情");
        mSubList = findViewById(R.id.subList);

        mTaskHelper = XLTaskHelper.instance(this);

        Intent intent = getIntent();
        mTaskId = intent.getLongExtra("taskId",0);
        hash = intent.getStringExtra("hash");
        torrentPath = intent.getStringExtra("torrentPath");
        mList = (List<TorrentInfoEntity>) intent.getSerializableExtra("subTasks");

        adapter = new TorrentTaskListAdapter(this,mList,mListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(RecyclerView.VERTICAL);

        mSubList.setLayoutManager(layoutManager);
        mSubList.setAdapter(adapter);

        //分割线
        mSubList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

        FloatingActionButton fab = findViewById(R.id.navToDetail);
        fab.setOnClickListener(view -> {
            if(null != this.torrentPath && this.torrentPath != ""){
                File torrentFile =  new File(this.torrentPath);
                if(torrentFile.exists()){
                    Intent intent1 = new Intent(SubTaskActivity.this,TorrentDetailActivity.class);
                    intent1.putExtra("path",this.torrentPath);
                    startActivity(intent1);
                }
            }else{
                Toast.makeText(SubTaskActivity.this,"文件已删除",Toast.LENGTH_LONG).show();
            }
        });

    }
}
