package com.wangky.video.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.wangky.video.MainActivity;
import com.wangky.video.R;
import com.wangky.video.adapter.TorrentFileListAdapter;
import com.wangky.video.adapter.TorrentTaskListAdapter;
import com.wangky.video.beans.TorrentInfoEntity;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.TorrentFileInfo;

import java.util.List;

public class SubTaskActivity extends AppCompatActivity {

    private RecyclerView mSubList;
    private TorrentTaskListAdapter adapter;

    private List<TorrentInfoEntity> mList;

    private XLTaskHelper mTaskHelper;


    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            int position = holder.getAdapterPosition();
            TorrentInfoEntity file = mList.get(position);
            String localUrl = mTaskHelper.getLocalUrl(file.getPath());

            Intent intent = new Intent(SubTaskActivity.this, PlayActivity.class);
            intent.putExtra("LOrientation",true);
            intent.putExtra("data",localUrl);
            intent.putExtra("title",file.getmFileName());
            startActivity(intent);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_task);
        setTitle("任务详情");
        mSubList = findViewById(R.id.subList);

        mTaskHelper = XLTaskHelper.instance(this);

        Intent intent = getIntent();

        mList = (List<TorrentInfoEntity>) intent.getSerializableExtra("subTasks");

        adapter = new TorrentTaskListAdapter(this,mList,mListener);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setOrientation(RecyclerView.VERTICAL);

        mSubList.setLayoutManager(layoutManager);
        mSubList.setAdapter(adapter);

        //分割线
        mSubList.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));

    }
}
