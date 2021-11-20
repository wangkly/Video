package com.wangky.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wangky.video.R;
import com.wangky.video.beans.TorrentInfoEntity;
import com.wangky.video.util.FileTools;

import java.util.List;

public class TorrentTaskListAdapter extends RecyclerView.Adapter<TorrentTaskListAdapter.FileViewHolder> {


    private Context mContext;


    public void setmFlieList(List<TorrentInfoEntity> mFlieList) {
        this.mFlieList = mFlieList;
    }

    private List<TorrentInfoEntity> mFlieList;

    private View.OnClickListener mListener;


    public TorrentTaskListAdapter(Context mContext, List<TorrentInfoEntity> mFlieList, View.OnClickListener mListener) {
        this.mContext = mContext;
        this.mFlieList = mFlieList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View item = LayoutInflater.from(mContext).inflate(R.layout.sub_task_item,parent,false);

        FileViewHolder fileViewHolder = new FileViewHolder(item);

        return fileViewHolder;
    }

    @Override
    public int getItemCount() {
        return mFlieList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {

        TorrentInfoEntity fileInfo = mFlieList.get(position);
        holder.fTitle.setText(fileInfo.getmFileName());
        holder.taskFileSize.setText(FileTools.convertFileSize(fileInfo.getmFileSize()));
    }


    /**
     * 添加数据
     * @param files
     */
    public void addFiles(List<TorrentInfoEntity> files){

        mFlieList.addAll(files);

        notifyDataSetChanged();
    }



    class FileViewHolder extends RecyclerView.ViewHolder{

        private TextView fTitle;
        private TextView taskFileSize;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fTitle = itemView.findViewById(R.id.file_title);
            taskFileSize = itemView.findViewById(R.id.task_file_size);
            itemView.setTag(this);
            itemView.setOnClickListener(mListener);

        }



    }

}
