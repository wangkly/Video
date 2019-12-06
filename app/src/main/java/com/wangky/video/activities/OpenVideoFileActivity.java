package com.wangky.video.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.wangky.video.R;
import com.wangky.video.util.FileUtils;

public class OpenVideoFileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_video_file);
        Uri uri =  getIntent().getData();
        String path = FileUtils.getFilePathByUri(OpenVideoFileActivity.this, uri);
        if(null != path){
            String name = FileUtils.getFileName(path);
            Intent intent = new Intent(OpenVideoFileActivity.this,PlayActivity.class);
            intent.putExtra("LOrientation",false);
            intent.putExtra("data",path);
            intent.putExtra("title",name);
            startActivity(intent);
            finish();
        }else {
            Toast.makeText(OpenVideoFileActivity.this,"打开文件失败",Toast.LENGTH_SHORT).show();
        }
    }
}
