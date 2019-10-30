package com.wangky.video.model;

import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.beans.TorrentInfoEntity;

import java.util.List;


public interface DownLoadModel {
    Boolean startTorrentTask(DownloadTaskEntity bt);
    Boolean startTorrentTask(String btpath);
    Boolean startTorrentTask(DownloadTaskEntity bt, int[] indexs);
    Boolean startUrlTask(String url);
    Boolean startTorrentTask(String btpath, int[] indexs);
    Boolean startTask(DownloadTaskEntity task);
    Boolean stopTask(DownloadTaskEntity task);
    Boolean deleTask(DownloadTaskEntity task, Boolean deleFile);
    Boolean deleTask(DownloadTaskEntity task, Boolean stopTask, Boolean deleFile);
    List<TorrentInfoEntity> getTorrentInfo(DownloadTaskEntity bt);
    List<TorrentInfoEntity> getTorrentInfo(String btpath);

    String getLocalUrl(String filePath);
}
