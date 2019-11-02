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


    /**
     * 如果二次选择同一个任务调用这个方法怎么办？
     * 是否要判断任务是否已经存在，是否合并？
     * @param btpath
     * @param indexs
     * @return
     */
    @Override
    public Boolean startTorrentTask(String btpath, int[] indexs) {
        DownloadTaskEntity task=new DownloadTaskEntity();
        TorrentInfo torrentInfo= XLTaskHelper.instance(MyApplication.getInstance()).getTorrentInfo(btpath);

        //不选默认，选择全部子任务
        if(indexs==null || indexs.length<=0) {
            int i = 0;
            indexs = new int[torrentInfo.mSubFileInfo.length];
            for (TorrentFileInfo torrent : torrentInfo.mSubFileInfo) {
                indexs[i++] = torrent.mFileIndex;
            }
        }
        //保存子任务信息 当前次选择的子任务信息
        List<TorrentInfoEntity> subs= getSubTaskInfo(torrentInfo,indexs);

        //判断当前是否已存在该任务，通过hash查询，不存在走正常逻辑，存在合并子任务
        DownloadTaskEntity already = DBTools.getInstance().findByHash(torrentInfo.mInfoHash);
        if(null != already){
            //任务列表已存在情况
            //先停止，再重新启动
            XLTaskHelper.instance(MyApplication.getInstance()).stopTask(already.getTaskId());

            List<TorrentInfoEntity> alreadySubs = already.getSubTasks();
            List<TorrentInfoEntity> target = new ArrayList<>();

            //alreadySubs 与subs 比对合并,去重
            for(TorrentInfoEntity sub : subs){

                boolean contain =false;
                for(TorrentInfoEntity info : alreadySubs){
                    if(sub.getmFileIndex() == info.getmFileIndex()){
                        contain = true;
                        break;
                    }
                }

                if(!contain){
                    target.add(sub);
                }
            }
            //合并
            alreadySubs.addAll(target);
            already.setSubTasks(alreadySubs);
            startTask(already);
            return true;
        }
        //以下是第一次创建，任务不存在情况
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
            task.setSubTasks(subs);
            DBTools.getInstance().saveBindingId(task);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    /**
     * 获取选中的子任务，进行保存
     * @param torrentInfo
     * @param indexs 选中的子任务
     * @return 子任务信息
     */
    public List<TorrentInfoEntity> getSubTaskInfo(TorrentInfo torrentInfo,int[] indexs){
        List<TorrentInfoEntity> subs = new ArrayList<>();

        for(int i = 0 ; i < indexs.length; i++){
            int j = indexs[i]; //indexs 里存的是 torrentInfo.mSubFileInfo 里对应的下标
            TorrentFileInfo torrent = torrentInfo.mSubFileInfo[j];

            TorrentInfoEntity tie=new TorrentInfoEntity();
            tie.setHash(torrent.hash);
            tie.setmFileIndex(torrent.mFileIndex);
            tie.setmFileName(torrent.mFileName);
            tie.setmFileSize(torrent.mFileSize);
            tie.setmSubPath(torrent.mSubPath);
            tie.setmRealIndex(torrent.mRealIndex);
            tie.setPath(Const.File_SAVE_PATH+
                    File.separator+torrentInfo.mMultiFileBaseFolder+
                    File.separator+torrent.mFileName);

            subs.add(tie);
        }

        return subs;
    }

    /**
     * 暂停的下载任务再次启动后taskId 会改变，不一定为原来的值
     * @param task
     * @return
     */
    @Override
    public Boolean startTask(DownloadTaskEntity task) {
        try {
            long taskId=0;
            if(task.getTaskType()==Const.BT_DOWNLOAD){
//                TorrentInfo torrentInfo= XLTaskHelper.instance(MyApplication.getInstance()).getTorrentInfo(task.getUrl());
                //取任务中已存在的子任务
                List<TorrentInfoEntity> subs = task.getSubTasks();
                int[] indexs = new int[subs.size()];
                int i=0;
//                indexs=new int[torrentInfo.mSubFileInfo.length];
//                for(TorrentFileInfo torrent:torrentInfo.mSubFileInfo) {
//                    indexs[i++]=torrent.mFileIndex;
//                }

                for(TorrentInfoEntity info : subs){
                    indexs[i++]=info.getmFileIndex();
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
            DBTools.getInstance().updateMainTask(task);
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
