package com.wangky.video.listeners;

public interface HttpRequestListener {

    void onSuccess(String result);

    void OnFailed(String msg);
}
