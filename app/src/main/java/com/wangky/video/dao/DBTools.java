package com.wangky.video.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.util.DBHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBTools {

   private SQLiteDatabase db;


    public DBTools() {
        this.db = DBHelper.getInstance().db();
    }

    public static DBTools getInstance(){

       return new DBTools();
   }



   public boolean saveBindingId(DownloadTaskEntity entity){
        boolean result = false;
       ContentValues values = new ContentValues();
       values.put("taskId",entity.getTaskId());
       values.put("mTaskStatus",entity.getmTaskStatus());
       values.put("mFileSize",entity.getmFileSize());
       values.put("mFileName",entity.getmFileName());
       values.put("taskType",entity.getTaskType());
       values.put("url",entity.getUrl());
       values.put("localPath",entity.getLocalPath());
       values.put("mDownloadSize",entity.getmDownloadSize());
       values.put("mDownloadSpeed",entity.getmDownloadSpeed());
       values.put("mDCDNSpeed",entity.getmDCDNSpeed());
       values.put("hash",entity.getHash());
       values.put("isFile",entity.getFile() ? 1:0);
       values.put("createDate",entity.getCreateDate().getTime());
       values.put("thumbnailPath",entity.getThumbnailPath());

      long count = db.insert("download",null,values);

      if(count != -1){
          result =true;
      }
        return result;
   }



    public boolean saveOrUpdate(DownloadTaskEntity entity){

        boolean result = false;



        return result;
    }




    public void delete(DownloadTaskEntity entity){


   }



   public List<DownloadTaskEntity> findALLTask(){
      List<DownloadTaskEntity> tasks = new ArrayList<>();

      Cursor cursor =  db.query("download",null,null,null,null,null,null);

      if(cursor !=null && cursor.moveToFirst()){

          do{
              DownloadTaskEntity  entity = new DownloadTaskEntity();
              entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
              entity.setTaskId(cursor.getLong(cursor.getColumnIndex("taskId")));
              entity.setmTaskStatus(cursor.getInt(cursor.getColumnIndex("mTaskStatus")));
              entity.setmFileSize(cursor.getLong(cursor.getColumnIndex("mFileSize")));
              entity.setmFileName(cursor.getString(cursor.getColumnIndex("mFileName")));
              entity.setTaskType(cursor.getInt(cursor.getColumnIndex("taskType")));
              entity.setUrl(cursor.getString(cursor.getColumnIndex("url")));
              entity.setLocalPath(cursor.getString(cursor.getColumnIndex("localPath")));
              entity.setmDownloadSize(cursor.getLong(cursor.getColumnIndex("mDownloadSize")));
              entity.setmDownloadSpeed(cursor.getLong(cursor.getColumnIndex("mDownloadSpeed")));
              entity.setmDCDNSpeed(cursor.getLong(cursor.getColumnIndex("mDCDNSpeed")));
              entity.setHash(cursor.getString(cursor.getColumnIndex("hash")));
              entity.setFile(cursor.getInt(cursor.getColumnIndex("isFile")) == 1);
              entity.setCreateDate(new Date(cursor.getLong(cursor.getColumnIndex("createDate"))));

              tasks.add(entity);

          }while (cursor.moveToNext());
      }

      return tasks;

   }



}
