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
import java.util.List;

public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListAdapter.ItemViewHolder> {


    private Context mContext;

    private List<DownloadTaskEntity> mTasks;


    public DownloadListAdapter(Context mContext, List<DownloadTaskEntity> mTasks) {
        this.mContext = mContext;
        this.mTasks = mTasks;
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
                .divide(new BigDecimal(entity.getmFileSize()))
                .setScale(2,BigDecimal.ROUND_DOWN);
        holder.percent.setText(String.valueOf(percent));
        holder.progressBar.setProgress(percent.intValue());

    }

    @Override
    public int getItemCount() {
        return mTasks.size();
    }





    public class ItemViewHolder extends RecyclerView.ViewHolder{

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
        }



    }


}
