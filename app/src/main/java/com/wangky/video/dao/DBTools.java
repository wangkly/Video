package com.wangky.video.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.beans.TorrentInfoEntity;
import com.wangky.video.util.DBHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DBTools {

   private static DBTools mDBTools;

   private SQLiteDatabase db;


    private DBTools() {
        this.db = DBHelper.getInstance().db();
    }

    public static DBTools getInstance(){
        if(null != mDBTools){
            return mDBTools;
        }
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


      //保存下子任务信息
       List<TorrentInfoEntity> subTasks = entity.getSubTasks();
       for (TorrentInfoEntity sub :subTasks){
           ContentValues subValues = new ContentValues();
           subValues.put("pid",count);
           subValues.put("mFileIndex",sub.getmFileIndex());
           subValues.put("mFileName",sub.getmFileName());
           subValues.put("mFileSize",sub.getmFileSize());
           subValues.put("path",sub.getPath());
           subValues.put("mSubPath",sub.getmSubPath());
           subValues.put("playUrl",sub.getPlayUrl());
           subValues.put("hash",sub.getHash());

           db.insert("subtask",null,subValues);
       }

      if(count != -1){
          result =true;
      }
        return result;
   }



    public boolean saveOrUpdate(DownloadTaskEntity entity){
        int id = entity.getId();
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


        long count= db.update("download",values,"id = ?",new String[]{String.valueOf(id)});

        //更新子任务信息
        //先删除之前的再插入新的
       int deleteCount = db.delete("subtask","pid = ?",new String[]{String.valueOf(id)});
        System.out.println(deleteCount);

        List<TorrentInfoEntity> subTasks = entity.getSubTasks();
        for (TorrentInfoEntity sub :subTasks){
           ContentValues subValues = new ContentValues();
            subValues.put("pid",id);
            subValues.put("mFileIndex",sub.getmFileIndex());
            subValues.put("mFileName",sub.getmFileName());
            subValues.put("mFileSize",sub.getmFileSize());
            subValues.put("path",sub.getPath());
            subValues.put("mSubPath",sub.getmSubPath());
            subValues.put("playUrl",sub.getPlayUrl());
            subValues.put("hash",sub.getHash());

          long insertCount = db.insert("subtask",null,subValues);
            System.out.println(insertCount);
        }


        if(count != -1){
            result =true;
        }

        return result;
    }


    /**
     * 只更新主任务
     * @param entity
     * @return
     */
    public boolean updateMainTask(DownloadTaskEntity entity){
        int id = entity.getId();
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

        long count= db.update("download",values,"id = ?",new String[]{String.valueOf(id)});

        if(count != -1){
            result =true;
        }

        return result;
    }




    public void delete(DownloadTaskEntity entity){
        int id = entity.getId();
        db.delete("download","id = ?",new String[]{String.valueOf(id)});
        db.delete("subtask","pid = ?",new String[]{String.valueOf(id)});
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


              //查询子任务
              Cursor subCursor =  db.query("subtask",null,"pid =?",new String[]{String.valueOf(entity.getId())} ,null,null,null);
              List<TorrentInfoEntity> subTasks = new ArrayList<>();
              if(null != subCursor && subCursor.moveToFirst()){

                  do{
                      TorrentInfoEntity sub = new TorrentInfoEntity();

                      sub.setmFileIndex(subCursor.getInt(subCursor.getColumnIndex("mFileIndex")));
                      sub.setmFileName(subCursor.getString(subCursor.getColumnIndex("mFileName")));
                      sub.setmFileSize(subCursor.getLong(subCursor.getColumnIndex("mFileSize")));
                      sub.setPath(subCursor.getString(subCursor.getColumnIndex("path")));
                      sub.setmSubPath(subCursor.getString(subCursor.getColumnIndex("mSubPath")));
                      sub.setPlayUrl(subCursor.getString(subCursor.getColumnIndex("playUrl")));
                      sub.setHash(subCursor.getString(subCursor.getColumnIndex("hash")));

                      subTasks.add(sub);

                  }while (subCursor.moveToNext());

              }


              entity.setSubTasks(subTasks);

              tasks.add(entity);

          }while (cursor.moveToNext());
      }

      cursor.close();

      return tasks;

   }


    /**
     * 通过has 值查找任务是否已经存在 下载列表
     * @param hash
     * @return
     */
   public DownloadTaskEntity findByHash(String hash){
     Cursor cursor =  db.query("download",null,"hash =?",new String[]{hash},null,null,null);
     if(null != cursor && cursor.moveToFirst()){
         //相同hash应该只会有一个
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

         //查询子任务
         Cursor subCursor =  db.query("subtask",null,"pid =?",new String[]{String.valueOf(entity.getId())} ,null,null,null);

         List<TorrentInfoEntity> subTasks = new ArrayList<>();
         if(null != subCursor && subCursor.moveToFirst()){

             do{
                 TorrentInfoEntity sub = new TorrentInfoEntity();

                 sub.setmFileIndex(subCursor.getInt(subCursor.getColumnIndex("mFileIndex")));
                 sub.setmFileName(subCursor.getString(subCursor.getColumnIndex("mFileName")));
                 sub.setmFileSize(subCursor.getLong(subCursor.getColumnIndex("mFileSize")));
                 sub.setPath(subCursor.getString(subCursor.getColumnIndex("path")));
                 sub.setmSubPath(subCursor.getString(subCursor.getColumnIndex("mSubPath")));
                 sub.setPlayUrl(subCursor.getString(subCursor.getColumnIndex("playUrl")));
                 sub.setHash(subCursor.getString(subCursor.getColumnIndex("hash")));

                 subTasks.add(sub);

             }while (subCursor.moveToNext());

         }

         cursor.close();
         entity.setSubTasks(subTasks);


        return  entity;
     }
     return null;
   }

}
