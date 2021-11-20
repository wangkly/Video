package com.wangky.video.task;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;

import com.wangky.video.util.Const;
import com.wangky.video.util.MD5Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class SaveThumbnailTask extends AsyncTask <List<String>,Void,Void>{


    @Override
    protected Void doInBackground(List<String>... lists) {
        String saveDir =  Const.THUMBNAIL_SAVE_PATH;
        List<String> paths =  lists[0];
        Bitmap bitmap;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        for (String path: paths){
            try{
                String md5Str = MD5Util.md5Encode32(path);
                File saveFile = new File(saveDir +File.separator+ md5Str);
                if(saveFile.exists()) {
                    continue;
                }

                //https://www.niwoxuexi.com/blog/cnbzlj/article/1017

                retriever.setDataSource(path);
                bitmap = retriever.getFrameAtTime(-1,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);

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
            File saveFile = new File(saveDir +File.separator+ md5Str);
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
