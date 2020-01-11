package com.wangky.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wangky.video.R;
import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.util.Const;
import com.wangky.video.util.FileUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.ItemViewHolder> {


    private Context mContext;

    private List<DownloadTaskEntity> mTasks;


    private OnOperationBtnClick mBtnClick;


    public DownloadListAdapter(Context mContext, List<DownloadTaskEntity> mTasks, OnOperationBtnClick mBtnClick) {
        this.mContext = mContext;
        this.mTasks = mTasks;
        this.mBtnClick = mBtnClick;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.download_item,null,false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {

        DownloadTaskEntity entity = mTasks.get(position);

        holder.fileName.setText(entity.getmFileName());
        holder.totalSize.setText(FileUtils.getFileSize(entity.getmFileSize()));
        holder.downloadSize.setText(FileUtils.getFileSize(entity.getmDownloadSize()));

        holder.speed.setText(FileUtils.downloadSpeed(entity.getmDownloadSpeed()));

        BigDecimal percent = new BigDecimal(entity.getmDownloadSize())
                .divide(new BigDecimal(entity.getmFileSize() != 0 ? entity.getmFileSize() : 1),2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100))
                .setScale(2,BigDecimal.ROUND_DOWN);


        holder.percent.setText(percent.compareTo(BigDecimal.valueOf(100)) > 0 ? "-" :percent +"%");
        holder.downloadStatus.setText(getStatusText(entity.getmTaskStatus()));
        holder.progressBar.setProgress(percent.compareTo(BigDecimal.valueOf(100)) > 0 ? 0 :percent.intValue());

        if(entity.getmTaskStatus() == Const.DOWNLOAD_STOP
                || entity.getmTaskStatus() == Const.DOWNLOAD_FAIL
                || entity.getmTaskStatus() == Const.DOWNLOAD_WAIT){
            //下载暂停，失败，等待
            holder.pause.setVisibility(View.GONE);
            holder.start.setVisibility(View.VISIBLE);
        }else if(entity.getmTaskStatus() == Const.DOWNLOAD_CONNECTION
                || entity.getmTaskStatus() == Const.DOWNLOAD_LOADING  ){
            //下载中，连接中
            holder.start.setVisibility(View.GONE);
            holder.pause.setVisibility(View.VISIBLE);
        }else {
            //已成功 ，都不显示
            holder.start.setVisibility(View.GONE);
            holder.pause.setVisibility(View.GONE);
        }


    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }


    public String getStatusText(int statusCode){
        String statusText;
        switch (statusCode){
            case Const.DOWNLOAD_STOP:
                statusText = "已停止";
                break;
            case Const.DOWNLOAD_FAIL:
                statusText = "下载失败";
                break;
            case Const.DOWNLOAD_WAIT:
                statusText = "等待中";
                break;
            case Const.DOWNLOAD_CONNECTION:
                statusText = "连接中";
                break;
            case Const.DOWNLOAD_LOADING:
                statusText = "下载中";
                break;
            case Const.DOWNLOAD_SUCCESS:
                statusText = "已完成";
                break;
            default:
                statusText = "连接中";
                break;
        }
        return statusText;
    }


    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView fileName;
        private TextView totalSize;
        private TextView downloadSize;
        private TextView speed;
        private TextView percent;
        private TextView downloadStatus;

        private ImageButton start;
        private ImageButton pause;

        private ProgressBar progressBar;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            totalSize = itemView.findViewById(R.id.totalSize);
            downloadSize = itemView.findViewById(R.id.downloadSize);
            speed = itemView.findViewById(R.id.speed);
            percent = itemView.findViewById(R.id.percent);
            downloadStatus = itemView.findViewById(R.id.download_status);
            start = itemView.findViewById(R.id.down_start);
            pause = itemView.findViewById(R.id.pause);

            progressBar = itemView.findViewById(R.id.down_progress);

            start.setOnClickListener(this);
            pause.setOnClickListener(this);

            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                DownloadTaskEntity entity = mTasks.get(position);
                mBtnClick.onDelete(entity);
                return true;
            });

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position < 0){
                return;
            }
            DownloadTaskEntity entity = mTasks.get(position);
            switch (v.getId()){
                case R.id.down_start:
                    mBtnClick.onStart(entity);
                    start.setVisibility(View.GONE);
                    pause.setVisibility(View.VISIBLE);
                    break;
                case R.id.pause:
                    mBtnClick.onPause(entity);
                    start.setVisibility(View.VISIBLE);
                    pause.setVisibility(View.GONE);
                    break;

                default:
                    mBtnClick.onDetail(entity);
                    break;

            }
        }
    }




    public interface OnOperationBtnClick{

        void onDelete(DownloadTaskEntity task);

        void onStart(DownloadTaskEntity task);

        void onPause(DownloadTaskEntity task);

        void onDetail(DownloadTaskEntity task);

    }


}
