package com.wangky.video.util;

import com.wangky.video.beans.DownloadTaskEntity;

public class DownUtil {
    private static DownUtil downUtil=null;
    private static boolean isLoopDown=false;
    public DownUtil(){

    }
    public static synchronized DownUtil getInstance() {
        if(downUtil==null){
            downUtil=new DownUtil();
        }
        return downUtil;
    }

    public static boolean isDownSuccess(DownloadTaskEntity task){
        long fileSize=task.getFile()?task.getmFileSize():task.getmFileSize()-10000;
        if (task.getmTaskStatus() == Const.DOWNLOAD_SUCCESS ||
                (task.getmFileSize()>0 && task.getmDownloadSize()>0 && fileSize<=task.getmDownloadSize())) {
            return  true;
        }
        return false;
    }

    public  boolean isIsLoopDown() {
        return isLoopDown;
    }

    public void setIsLoopDown(boolean isLoopDown) {
        DownUtil.isLoopDown = isLoopDown;
    }
}
