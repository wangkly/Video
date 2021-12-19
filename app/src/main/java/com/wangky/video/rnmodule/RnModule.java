package com.wangky.video.rnmodule;

import static com.wangky.video.util.Const.DOWNLOAD_FAIL;
import static com.wangky.video.util.Const.DOWNLOAD_SUCCESS;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.wangky.video.MyApplication;
import com.wangky.video.activities.TorrentDetailActivity;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Timer;
import java.util.TimerTask;

public class RnModule extends ReactContextBaseJavaModule {

    private XLTaskHelper mTaskHelper = XLTaskHelper.instance(MyApplication.getInstance());

    private String savePath;

    private TimerTask timerTask;

    private Timer  timer;

    public RnModule(ReactApplicationContext context) {
        super(context);
    }

    @NonNull
    @Override
    public String getName() {
        return "RnModule";
    }

    @ReactMethod
    public void addMagnetTask(String magnet){
        Log.d("RnModule","receive magnet:"+magnet);
        downloadMagnet(magnet);
    }

    @ReactMethod
    public void readJson(Promise promise){
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "rn/data.json";
        File file =  new File(path);
        if(file.exists()){
            try {
                InputStream inputStream = new FileInputStream(file);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line ;
                StringBuilder builder = new StringBuilder();
                while ((line = bufferedReader.readLine()) != null ){
                    builder.append(line);
                }

                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
                JSONArray jsonArray = new JSONArray(builder.toString());
                //https://stackoverflow.com/questions/33709070/what-is-cause-for-this-issue-cannot-convert-argument-of-type-class-org-json-jso
                //When working Native bridges, callbacks should be invoked with WritableNative components from the com.facebook.react.bridge package.
                //Instead of a JSONObject, use a WritableNativeMap.
                //Instead of a JSONArray, use a WritableNativeArray.
                WritableNativeArray array =  new WritableNativeArray();
                for(int i = 0; i < jsonArray.length(); i++){
                    WritableMap info = new WritableNativeMap();
                    JSONObject obj = jsonArray.getJSONObject(i);
                    info.putString("title",obj.getString("title"));
                    info.putString("imgUrl",obj.getString("img"));
                    info.putString("magnet",obj.getString("magnet"));
                    array.pushMap(info);
                }

                promise.resolve(array);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 下载magnet对应的种子文件再利用种子文件进行下载
     * @param url
     */
    public void downloadMagnet(String url){
        String fileName = mTaskHelper.getFileName(url);
        savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "MyVideoDownload";
        File file = new File(savePath);
        if(!file.exists()){
            file.mkdirs();
        }

        try {
            long taskId =  mTaskHelper.addMagentTask(url,savePath,fileName);
            //获取下载的种子文件的内存路径，跳转到TorrentDetailActivity
            String torrentPath = savePath + File.separator + fileName;
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    XLTaskInfo taskInfo =  mTaskHelper.getTaskInfo(taskId);
                    if(taskInfo.mTaskStatus == DOWNLOAD_SUCCESS){
                        mTaskHelper.stopTask(taskId);
                        timer.cancel();
                        Intent intent = new Intent(MyApplication.getInstance(), TorrentDetailActivity.class);
                        intent.putExtra("type","torrent");
                        intent.putExtra("path",torrentPath);
                        getCurrentActivity().startActivity(intent);
                    }else if(taskInfo.mTaskStatus == DOWNLOAD_FAIL){
                        Toast.makeText(MyApplication.getInstance(),"链接解析失败",Toast.LENGTH_SHORT).show();
                        mTaskHelper.stopTask(taskId);
                        timer.cancel();
                    }
                }
            };

            timer.schedule(timerTask,1000,1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
