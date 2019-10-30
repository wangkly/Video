package com.wangky.video.model;

import com.wangky.video.MyApplication;
import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.beans.TorrentInfoEntity;
import com.wangky.video.dao.DBTools;
import com.wangky.video.util.Const;
import com.wangky.video.util.FileTools;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.TorrentFileInfo;
import com.xunlei.downloadlib.parameter.TorrentInfo;
import com.xunlei.downloadlib.parameter.XLTaskInfo;


import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DownLoadModelImp implements DownLoadModel {
    public DownLoadModelImp(){

    }
    @Override
    public Boolean startTorrentTask(DownloadTaskEntity bt) {
        String path=bt.getUrl();
        try {
            DBTools.getInstance().delete(bt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return startTorrentTask(path,null);
    }

    @Override
    public Boolean startTorrentTask(String btpath) {
        return startTorrentTask(btpath,null);
    }
    @Override
    public Boolean startTorrentTask(DownloadTaskEntity bt, int[] indexs) {
        String path=bt.getLocalPath()+ File.separator+bt.getmFileName();
        return startTorrentTask(path,indexs);
    }

    @Override
    public Boolean startUrlTask(String url) {
        DownloadTaskEntity task=new DownloadTaskEntity();
        task.setTaskType(Const.URL_DOWNLOAD);
        task.setUrl(url);
        task.setLocalPath(Const.File_SAVE_PATH);
        try {
            long taskId = XLTaskHelper.instance(MyApplication.getInstance()).addThunderTask(url,Const.File_SAVE_PATH,null);
            XLTaskInfo taskInfo = XLTaskHelper.instance(MyApplication.getInstance()).getTaskInfo(taskId);
            task.setmFileName(XLTaskHelper.instance(MyApplication.getInstance()).getFileName(url));
            task.setmFileSize(taskInfo.mFileSize);
            task.setmTaskStatus(taskInfo.mTaskStatus);
            task.setTaskId(taskId);
            task.setmDCDNSpeed(taskInfo.mAdditionalResDCDNSpeed);
            task.setmDownloadSize(taskInfo.mDownloadSize);
            task.setmDownloadSpeed(taskInfo.mDownloadSpeed);
            task.setFile(true);
            task.setCreateDate(new Date());
            DBTools.getInstance().saveBindingId(task);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean startTorrentTask(String btpath, int[] indexs) {
        DownloadTaskEntity task=new DownloadTaskEntity();
        TorrentInfo torrentInfo= XLTaskHelper.instance(MyApplication.getInstance()).getTorrentInfo(btpath);
        if(indexs==null || indexs.length<=0) {
            int i = 0;
            indexs = new int[torrentInfo.mSubFileInfo.length];
            for (TorrentFileInfo torrent : torrentInfo.mSubFileInfo) {
                indexs[i++] = torrent.mFileIndex;
            }
        }
        String savePath= Const.File_SAVE_PATH;
        if(torrentInfo.mIsMultiFiles) {
            savePath += File.separator + torrentInfo.mMultiFileBaseFolder;
            task.setmFileName(torrentInfo.mMultiFileBaseFolder);
        }else{
            if(torrentInfo.mSubFileInfo.length>1) {
                savePath += File.separator + FileTools.getFileNameWithoutSuffix(btpath);
                task.setmFileName(FileTools.getFileNameWithoutSuffix(btpath));
            }else{
                task.setmFileName(torrentInfo.mSubFileInfo[0].mFileName);
            }
        }
        long taskId= 0;
        try {
            taskId = XLTaskHelper.instance(MyApplication.getInstance()).addTorrentTask(btpath, savePath,indexs);
            XLTaskInfo taskInfo = XLTaskHelper.instance(MyApplication.getInstance()).getTaskInfo(taskId);
            task.setLocalPath(savePath);
            task.setFile(!torrentInfo.mIsMultiFiles);
            task.setHash(torrentInfo.mInfoHash);
            task.setUrl(btpath);
            task.setmFileSize(taskInfo.mFileSize);
            task.setmTaskStatus(taskInfo.mTaskStatus);
            task.setTaskId(taskId);
            task.setmDCDNSpeed(taskInfo.mAdditionalResDCDNSpeed);
            task.setmDownloadSize(taskInfo.mDownloadSize);
            task.setmDownloadSpeed(taskInfo.mDownloadSpeed);
            task.setTaskType(Const.BT_DOWNLOAD);
            task.setCreateDate(new Date());
            DBTools.getInstance().saveBindingId(task);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean startTask(DownloadTaskEntity task) {
        try {
            long taskId=0;
            if(task.getTaskType()==Const.BT_DOWNLOAD){
                TorrentInfo torrentInfo= XLTaskHelper.instance(MyApplication.getInstance()).getTorrentInfo(task.getUrl());
                int i=0;
                int[] indexs=new int[torrentInfo.mSubFileInfo.length];
                for(TorrentFileInfo torrent:torrentInfo.mSubFileInfo) {
                    indexs[i++]=torrent.mFileIndex;
                }
                taskId = XLTaskHelper.instance(MyApplication.getInstance()).addTorrentTask(task.getUrl(), task.getLocalPath(),indexs);
            }else if(task.getTaskType()==Const.URL_DOWNLOAD){
                taskId = XLTaskHelper.instance(MyApplication.getInstance()).addThunderTask(task.getUrl(), task.getLocalPath(), null);
            }
            XLTaskInfo taskInfo = XLTaskHelper.instance(MyApplication.getInstance()).getTaskInfo(taskId);
            task.setmFileSize(taskInfo.mFileSize);
            task.setTaskId(taskId);
            task.setmTaskStatus(taskInfo.mTaskStatus);
            DBTools.getInstance().saveOrUpdate(task);
            if(taskInfo.mTaskId==0)
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean stopTask(DownloadTaskEntity task) {
        try {
            XLTaskHelper.instance(MyApplication.getInstance()).stopTask(task.getTaskId());
            task.setmTaskStatus(Const.DOWNLOAD_STOP);
            task.setmDownloadSpeed(0);
            task.setmDCDNSpeed(0);
            DBTools.getInstance().saveOrUpdate(task);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean deleTask(DownloadTaskEntity task, Boolean deleFile) {
        try {
            DBTools.getInstance().delete(task);
            if(deleFile){
                if(task.getFile()){
                    FileTools.deleteFile(task.getLocalPath()+ File.separator+task.getmFileName());
                }else{
                    FileTools.deleteDir(task.getLocalPath());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Boolean deleTask(DownloadTaskEntity task, Boolean stopTask, Boolean deleFile) {
        if(stopTask){
            XLTaskHelper.instance(MyApplication.getInstance()).stopTask(task.getTaskId());
        }
        return deleTask(task,deleFile);
    }

    @Override
    public List<TorrentInfoEntity> getTorrentInfo(DownloadTaskEntity bt) {
        String path=bt.getLocalPath()+ File.separator+bt.getmFileName();
        return getTorrentInfo(path);
    }

    @Override
    public List<TorrentInfoEntity> getTorrentInfo(String btpath) {
        TorrentInfo torrentInfo= XLTaskHelper.instance(MyApplication.getInstance()).getTorrentInfo(btpath);
        List<TorrentInfoEntity> list=new ArrayList<>();
        for(TorrentFileInfo torrent:torrentInfo.mSubFileInfo){
            TorrentInfoEntity tie=new TorrentInfoEntity();
            tie.setHash(torrent.hash);
            tie.setmFileIndex(torrent.mFileIndex);
            tie.setmFileName(torrent.mFileName);
            tie.setmFileSize(torrent.mFileSize);
            tie.setmSubPath(torrent.mSubPath);
            tie.setmRealIndex(torrent.mRealIndex);
            tie.setPath(Const.File_SAVE_PATH+
                    File.separator+torrentInfo.mMultiFileBaseFolder+
                    File.separator+torrent.mSubPath+File.separator+torrent.mFileName);
            list.add(tie);
        }
        return list;
    }


    @Override
    public String getLocalUrl(String filePath) {
        String path = XLTaskHelper.instance(MyApplication.getInstance()).getLocalUrl(filePath);
        return path;
    }
}
