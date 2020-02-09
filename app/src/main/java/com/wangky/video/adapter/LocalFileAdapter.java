package com.wangky.video.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wangky.video.R;
import com.wangky.video.beans.LocalFile;
import com.wangky.video.enums.FileTyp;
import com.wangky.video.util.DateUtil;
import com.wangky.video.util.FileTools;

import java.util.List;

public class LocalFileAdapter  extends RecyclerView.Adapter<LocalFileAdapter.FileItemHolder> {


    private Context mContext;

    private List<LocalFile> fileList;

    private onItemClickListener listener;


    public LocalFileAdapter(Context mContext, List<LocalFile> fileList, onItemClickListener listener) {
        this.mContext = mContext;
        this.fileList = fileList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FileItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view =  LayoutInflater.from(mContext).inflate(R.layout.local_file,parent,false);
       return  new FileItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FileItemHolder holder, int position) {

        LocalFile file = fileList.get(position);

        if(file.getType().equals(FileTyp.FOLDER) || file.getType().equals(FileTyp.BACK)){
            holder.fileIcon.setImageResource(R.drawable.ic_folder);
            holder.modifyTime.setVisibility(View.GONE);
            holder.fileSize.setVisibility(View.GONE);
            holder.optBtn.setVisibility(View.VISIBLE);

        }else{
            holder.fileIcon.setImageResource(R.drawable.ic_file);
            holder.fileSize.setText(FileTools.convertFileSize(file.getFileSize()));
            holder.optBtn.setVisibility(View.GONE);
        }

        holder.fileName.setText(file.getName());

        if(!file.getType().equals(FileTyp.BACK)){
            holder.modifyTime.setText(DateUtil.FormatTimeStamp(file.getModifyTime()));
        }

    }



    @Override
    public int getItemCount() {

        return fileList.size();
    }


    public class FileItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private ImageView fileIcon;

        private TextView fileName;

        private TextView fileSize;

        private TextView modifyTime;

        private ImageButton optBtn;

        public FileItemHolder(@NonNull View itemView) {
            super(itemView);
            fileIcon= itemView.findViewById(R.id.file_icon);
            fileName = itemView.findViewById(R.id.file_name);
            fileSize = itemView.findViewById(R.id.file_size);
            modifyTime = itemView.findViewById(R.id.modify_time);
            optBtn = itemView.findViewById(R.id.opt_btn);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if(position < 0){
                return;
            }
            LocalFile file = fileList.get(position);
            listener.onItemClicked(file);
        }
    }



    public void refreshFileList(List<LocalFile> files){
        fileList.clear();
        fileList.addAll(files);
        notifyDataSetChanged();
    }



    public interface onItemClickListener{

        void onItemClicked(LocalFile file);

    }


}
