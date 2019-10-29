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

        holder.speed.setText(FileUtils.getFileSize(entity.getmDownloadSpeed()));

        BigDecimal percent = new BigDecimal(entity.getmDownloadSize())
                .divide(new BigDecimal(entity.getmFileSize()),2, RoundingMode.HALF_UP)
                .setScale(2,BigDecimal.ROUND_DOWN);
        holder.percent.setText(String.valueOf(percent));
        holder.progressBar.setProgress(percent.intValue());

    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }





    public class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView fileName;
        private TextView totalSize;
        private TextView downloadSize;
        private TextView speed;
        private TextView percent;

        private ImageButton start;
        private ImageButton pause;
        private ImageButton delete;

        private ProgressBar progressBar;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.file_name);
            totalSize = itemView.findViewById(R.id.totalSize);
            downloadSize = itemView.findViewById(R.id.downloadSize);
            speed = itemView.findViewById(R.id.speed);
            percent = itemView.findViewById(R.id.percent);

            start = itemView.findViewById(R.id.down_start);
            pause = itemView.findViewById(R.id.pause);
            delete = itemView.findViewById(R.id.delete);

            progressBar = itemView.findViewById(R.id.down_progress);

            start.setOnClickListener(this);
            pause.setOnClickListener(this);
            delete.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            DownloadTaskEntity entity = mTasks.get(position);
            switch (v.getId()){
                case R.id.delete:
                    mBtnClick.onDelete(entity);
                    break;
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
                    break;

            }
        }
    }




    public interface OnOperationBtnClick{

        void onDelete(DownloadTaskEntity task);

        void onStart(DownloadTaskEntity task);

        void onPause(DownloadTaskEntity task);

    }


}
