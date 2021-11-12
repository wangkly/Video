package com.wangky.video.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.wangky.video.MyApplication;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper dbHelper = null;

    public final static  String DB_NAME = "video.db";
    public final static  int DB_VERSION = 1;

    public static final String CREATE_DOWNLOAD ="create table download (" +
            "id integer primary key autoincrement," +
            "taskId integer , mTaskStatus integer, " +
            "mFileSize integer, mFileName text, " +
            "taskType integer, url text, localPath text, torrentPath text," +
            "mDownloadSize integer, mDownloadSpeed integer, " +
            "mDCDNSpeed integer, hash text, isFile integer, createDate integer, thumbnailPath text)";


    public static final String CREATE_SUBTASKS ="create table subtask(id integer primary key autoincrement, pid integer," +
                    "mFileIndex integer,mFileName text,mFileSize integer,path text,mSubPath text,playUrl text,hash text)";


    public static final String RECENT_VISIT ="create table recent_visit(id integer primary key autoincrement ,path text,createTime integer)";

    private DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }


    public static synchronized DBHelper getInstance(){
        if(dbHelper == null){
            dbHelper = new DBHelper(MyApplication.getInstance());
        }
        return dbHelper;
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DOWNLOAD);
        db.execSQL(CREATE_SUBTASKS);
        db.execSQL(RECENT_VISIT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists download");
        db.execSQL("drop table if exists subtask");
        db.execSQL("drop table if exists recent_visit");
    }



    public SQLiteDatabase db(){
       return dbHelper.getWritableDatabase();
    }


}
