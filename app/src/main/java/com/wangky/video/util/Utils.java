package com.wangky.video.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.wangky.video.beans.LocalVideoItem;

import java.util.ArrayList;
import java.util.List;

public class Utils {



    public static List<LocalVideoItem> queryLocalVideos(Context context){

      List<LocalVideoItem> list = new ArrayList<>();

      ContentResolver resolver =  context.getContentResolver();

        String[] projections = {
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.DURATION,
                MediaStore.Video.Media.ARTIST,
                MediaStore.Video.Media.DATE_ADDED,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.WIDTH,
                MediaStore.Video.Media.HEIGHT
        };

        String[] thumbColumns = {
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID
        };


        Cursor cursor =  resolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,projections,null,null,null);


        if(null != cursor && cursor.moveToFirst()){

            do{
               long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
               String title =  cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
               String data = cursor.getString(cursor.getColumnIndex( MediaStore.Video.Media.DATA));
               long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
               String artist = cursor.getString(cursor.getColumnIndex( MediaStore.Video.Media.ARTIST));
               long addTime = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
               long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
               long width = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH));
               long height = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT));
               String thumbUri = null;

               Cursor thumbCursor = resolver.query(MediaStore.Video.Thumbnails.EXTERNAL_CONTENT_URI,thumbColumns, MediaStore.Video.Thumbnails.VIDEO_ID + "=?",new String[]{String.valueOf(id)},null,null);

               if(thumbCursor.moveToFirst()){
                   thumbUri = thumbCursor.getString(thumbCursor.getColumnIndex(MediaStore.Video.Thumbnails.DATA));
               }


               LocalVideoItem item = new LocalVideoItem(id,title,data,duration,artist,addTime,size,width,height,thumbUri);
               list.add(item);


            }while (cursor.moveToNext());

        }



        return list;

    }



}
