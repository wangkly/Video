package com.wangky.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.wangky.video.R;
import com.wangky.video.util.FileUtils;
import com.xunlei.downloadlib.parameter.TorrentFileInfo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class TorrentFileListAdapter extends RecyclerView.Adapter<TorrentFileListAdapter.FileViewHolder> {


    private Context mContext;

    private List<TorrentFileInfo> mFlieList;

    private View.OnClickListener mListener;


    public TorrentFileListAdapter(Context mContext, List<TorrentFileInfo> mFlieList, View.OnClickListener mListener) {
        this.mContext = mContext;
        this.mFlieList = mFlieList;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

       View item = LayoutInflater.from(mContext).inflate(R.layout.file_item,parent,false);

        FileViewHolder fileViewHolder = new FileViewHolder(item);

        return fileViewHolder;
    }

    @Override
    public int getItemCount() {
        return mFlieList.size();
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {

        TorrentFileInfo fileInfo = mFlieList.get(position);
        holder.fTitle.setText(fileInfo.mFileName);
        String fileSize = FileUtils.getFileSize(fileInfo.mFileSize);
        holder.fSize.setText(fileSize);
        boolean isVideo = FileUtils.isVideoFile(fileInfo.mFileName);
        if(isVideo){
            holder.imgBtn.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 添加数据
     * @param files
     */
    public void addFiles(List<TorrentFileInfo> files){

        mFlieList.addAll(files);

        notifyDataSetChanged();
    }



    class FileViewHolder extends RecyclerView.ViewHolder{

        private TextView fTitle;

        private TextView fSize;

        private ImageButton imgBtn;

        public FileViewHolder(@NonNull View itemView) {
            super(itemView);
            fTitle = itemView.findViewById(R.id.file_title);
            fSize = itemView.findViewById(R.id.f_size);
            imgBtn = itemView.findViewById(R.id.f_play);
            itemView.setTag(this);
            itemView.setOnClickListener(mListener);

        }



    }

}
