package com.wangky.video.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.wangky.video.R;

public class PlayActivity extends AppCompatActivity {


    private GestureDetectorCompat mGestureDetector;

    private SimpleExoPlayer player;

    private ImageButton mBack;

    private TextView mTitle;


    private GestureDetector.SimpleOnGestureListener myGestureListener = new GestureDetector.SimpleOnGestureListener(){
//        @Override
//        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
//
//            Log.d("-------->playing","scroll");
//            return super.onScroll(e1, e2, distanceX, distanceY);
//        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {

            Log.i("-------->playing","onDoubleTap");
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d("-------->playing","onSingleTapUp");
            return super.onSingleTapUp(e);
        }


        @Override
        public boolean onDown(MotionEvent e) {

            Log.d("-------->playing","onSingleTapUp");

            return super.onDown(e);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_play);


        mGestureDetector = new GestureDetectorCompat(PlayActivity.this,myGestureListener);

        Intent intent = getIntent();

        String data = intent.getStringExtra("data");

        Boolean orientation = intent.getBooleanExtra("orientation",false);

        if(orientation){
            //横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        PlayerView playerView = findViewById(R.id.player);

        player = ExoPlayerFactory.newSimpleInstance(this);


        playerView.setPlayer(player);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this,"Video"));


        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).
                createMediaSource(Uri.parse(data));

        player.setPlayWhenReady(true);
        player.prepare(videoSource);


        mBack = findViewById(R.id.m_back);

        mTitle = findViewById(R.id.m_title);

        mTitle.setText("video_player");

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });



    }

    @Override
    protected void onDestroy() {

        player.release();

        super.onDestroy();
    }




    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mGestureDetector.onTouchEvent(event)){

            return true;
        }

        return false;
    }
}
