package com.wangky.video.listeners;

/**
 * 播放界面用户操作监听
 *
 */
public interface UserOperationListener{

    void onViewTap();

    void onVideoVolumeChange(float percent);

    void onViewBrightnessChange(float percent);

    void onVideoProgressChange(int type ,float percent);

    void onOperationEnd();

    void onOperationStart();

}