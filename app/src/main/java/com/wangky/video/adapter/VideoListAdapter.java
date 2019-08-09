package com.wangky.video.adapter;

import android.content.Context;
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

import java.util.List;

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


        Glide.with(mContext).load(item.getCoverImg()).into(holder.image);

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





}
