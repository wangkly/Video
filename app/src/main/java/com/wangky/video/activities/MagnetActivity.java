package com.wangky.video.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.wangky.video.R;
import com.xunlei.downloadlib.XLTaskHelper;
import com.xunlei.downloadlib.parameter.XLTaskInfo;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static com.wangky.video.util.Const.DOWNLOAD_FAIL;
import static com.wangky.video.util.Const.DOWNLOAD_SUCCESS;

public class MagnetActivity extends AppCompatActivity {

    private TextInputEditText mInput;

    private Button mAnalysisBtn;

    private XLTaskHelper mTaskHelper;

    private String savePath;

    private TimerTask timerTask;

    private Timer  timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_magnet);
        setTitle(R.string.action_magnet);

        mTaskHelper= XLTaskHelper.instance(this);

        mInput = findViewById(R.id.url_input);
        mAnalysisBtn = findViewById(R.id.analysis);


        mAnalysisBtn.setOnClickListener(v -> {
           String url = String.valueOf(mInput.getText());

           if(TextUtils.isEmpty(url)){
               Toast.makeText(MagnetActivity.this,"请填写链接",Toast.LENGTH_LONG).show();
               return;
           }

           if(url.startsWith("magnet:")){
               downloadMagnet(url);
           }else{
               Toast.makeText(MagnetActivity.this,"请填写正确的磁力链接",Toast.LENGTH_SHORT).show();
           }


        });

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
                        Intent intent = new Intent(MagnetActivity.this,TorrentDetailActivity.class);
                        intent.putExtra("type","torrent");
                        intent.putExtra("path",torrentPath);
                        startActivity(intent);
                    }else if(taskInfo.mTaskStatus == DOWNLOAD_FAIL){
                        Toast.makeText(MagnetActivity.this,"链接解析失败",Toast.LENGTH_SHORT).show();
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
