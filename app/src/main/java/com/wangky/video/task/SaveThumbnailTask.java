package com.wangky.video.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.wangky.video.util.Const;
import com.wangky.video.util.MD5Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import wseemann.media.FFmpegMediaMetadataRetriever;

public class SaveThumbnailTask extends AsyncTask <List<String>,Void,Void>{


    @Override
    protected Void doInBackground(List<String>... lists) {
       List<String> paths =  lists[0];
        Bitmap bitmap;
        FFmpegMediaMetadataRetriever retriever = new  FFmpegMediaMetadataRetriever();
        for (String path: paths){
            try{
                retriever.setDataSource(path); //file's path
                bitmap = retriever.getFrameAtTime(100000,FFmpegMediaMetadataRetriever.OPTION_CLOSEST_SYNC );
                if(null != bitmap){
                    //本地缓存一份
                    saveBitmapToLocal(bitmap,path);
                }

            }catch (Exception e) {
                e.printStackTrace();
            }

        }
        retriever.release();
        return null;
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
