package com.wangky.video.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.wangky.video.R;
import com.wangky.video.beans.LocalVideoItem;
import com.wangky.video.util.Const;
import com.wangky.video.util.MD5Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.VideoViewHolder> {


    private List<LocalVideoItem> mList;

    private Context mContext;


    private View.OnClickListener mListener;


    public VideoListAdapter(Context mContext,List<LocalVideoItem> mList,View.OnClickListener listener) {
        this.mList = mList;
        this.mContext = mContext;
        this.mListener = listener;
    }



    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(mContext).inflate(R.layout.video_item,parent,false);


        VideoViewHolder holder = new VideoViewHolder(view);


        return holder;
    }



    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {

        LocalVideoItem item  = mList.get(position);
        File file = new File(Const.THUMBNAIL_SAVE_PATH+File.separator+MD5Util.md5Encode32(item.getData())+".webp");
        if(null !=item.getCoverImg()){
            Glide.with(mContext).load(item.getCoverImg()).into(holder.image);
        }else if(file.exists()){//查看是否有本地缓存
           Glide.with(mContext).load(file).into(holder.image);

        }else {
            Bitmap bitmap = this.getVideoThumbnailIfNull(item.getData());
            Glide.with(mContext).load(bitmap).into(holder.image);
        }

        holder.title.setText(item.getTitle());

    }




    @Override
    public int getItemCount() {
        return mList.size();
    }



    public LocalVideoItem getItemAtPosition(int position){

        return mList.get(position);
    }


    class VideoViewHolder extends RecyclerView.ViewHolder{


        private ImageView image;

        private TextView title;


        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.video_cover);

            title = itemView.findViewById(R.id.video_title);

            itemView.setTag(this);
            itemView.setOnClickListener(mListener);
        }


    }


    /**
     * 如果通过contentProvider获取不到视频的缩略图，
     * 通过这个方法再去查找一次
     * @param filePath 文件的路径
     * @return bitmap 视频文件缩略图
     */
    public Bitmap getVideoThumbnailIfNull(String filePath){
        Bitmap bitmap = null;
            FFmpegMediaMetadataRetriever retriever = new  FFmpegMediaMetadataRetriever();
            try {
                retriever.setDataSource(filePath); //file's path
                bitmap = retriever.getFrameAtTime(100000,FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC );
                if(null != bitmap){
                    //本地缓存一份
                    saveBitmapToLocal(bitmap,filePath);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally{
                retriever.release();
            }
           return bitmap;
    }

    /**
     * 将bitmap保存到本地存储
     * @param bitmap
     */
    public void saveBitmapToLocal(Bitmap bitmap,String filePath){
       String saveDir =  Const.THUMBNAIL_SAVE_PATH;
       File dir = new File(saveDir);
       if(!dir.exists()){
           dir.mkdirs();
       }
       String md5Str = MD5Util.md5Encode32(filePath);
       try {
           File saveFile = new File(saveDir +File.separator+ md5Str +".webp");
            if(saveFile.exists()) {
                return;
            }else{
                saveFile.createNewFile();
            }
            FileOutputStream fo = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.WEBP,50,fo);
            fo.flush();
            fo.close();
           } catch (IOException e) {
               e.printStackTrace();
           }finally {
       }
    }

}
