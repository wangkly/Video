package com.wangky.video;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.wangky.video.activities.DownloadActivity;
import com.wangky.video.activities.MagnetActivity;
import com.wangky.video.activities.PlayActivity;
import com.wangky.video.activities.TorrentDetailActivity;
import com.wangky.video.adapter.VideoListAdapter;
import com.wangky.video.beans.LocalVideoItem;
import com.wangky.video.util.FileUtils;
import com.wangky.video.util.Utils;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {


    private static final  int PERMISSION_REQUESTCODE = 1;

    private final int OPEN_FILE_MANAGER = 2;

    private RecyclerView mRecyclerView;

    private VideoListAdapter adapter;

    private long time = 0;


    private View.OnClickListener mListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerView.ViewHolder holder = (RecyclerView.ViewHolder) v.getTag();
            int position =  holder.getAdapterPosition();

            LocalVideoItem item = adapter.getItemAtPosition(position);
            Boolean LOrientation = item.getWidth() > item.getHeight();//是否横屏播放
            String data = item.getData();
            String title = item.getTitle();

            Intent intent = new Intent(MainActivity.this, PlayActivity.class);
            intent.putExtra("LOrientation",LOrientation);
            intent.putExtra("data",data);
            intent.putExtra("title",title);
            startActivity(intent);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        ActionBar actionBar = getSupportActionBar();

//        if(null != actionBar){
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeAsUpIndicator(R.drawable.ic_heart);
//        }


//       FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        mRecyclerView = findViewById(R.id.video_list);

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUESTCODE);
        }else {
            remainOperation();
        }



    }



    public void remainOperation(){


        List<LocalVideoItem> mlist = Utils.queryLocalVideos(getApplicationContext());

        adapter= new VideoListAdapter(MainActivity.this,mlist,mListener);

        GridLayoutManager layoutManager = new GridLayoutManager(MainActivity.this,2);


        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(layoutManager);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_open) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(intent,OPEN_FILE_MANAGER);

            return true;
        }

        if(id == R.id.download){
            Intent intent = new Intent(MainActivity.this, DownloadActivity.class);
            startActivity(intent);
        }

        if(id == R.id.action_magnet){
            Intent intent = new Intent(MainActivity.this, MagnetActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case PERMISSION_REQUESTCODE:
                if(PackageManager.PERMISSION_GRANTED == grantResults[0]){
                    remainOperation();
                }

                break;
            default:


                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case OPEN_FILE_MANAGER:
                    Uri uri = data.getData();
                    String path = FileUtils.getFilePathByUri(MainActivity.this, uri);

                    Intent intent = new Intent(MainActivity.this,TorrentDetailActivity.class);
                    intent.putExtra("type","torrent");
                    intent.putExtra("path",path);

                    startActivity(intent);
//                    DownloadTask task = new DownloadTask(MainActivity.this);
//                    task.execute(path);

                    break;


                default:

                    break;
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){ //按下返回键
            if(System.currentTimeMillis() - time > 1000){
                Toast.makeText(this,R.string.back_home_tip,Toast.LENGTH_LONG).show();
                time = System.currentTimeMillis();
            }else {
                 Intent intent = new Intent(Intent.ACTION_MAIN);
                 intent.addCategory(Intent.CATEGORY_HOME);
                 startActivity(intent);
            }

            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
}
