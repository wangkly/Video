package com.wangky.video.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.wangky.video.MyPlayerView;
import com.wangky.video.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class PlayActivity extends AppCompatActivity {

    private final String TAG = "PlayActivity.class";


    private SimpleExoPlayer player;
    private ImageButton mBack;
    private TextView mTitle;
    private ImageButton mToggle;
    //是否横屏播放
    private Boolean mLOrientation =false ;

    private float mBrightness = -1f;
    private AudioManager audioManager;
    private int maxVolume = 0;
    private int currentVolume = -1;

    private LinearLayout brightness;

    private LinearLayout volume_view;

    private TextView br_percent;

    private TextView vo_percent;



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            float percent = (float) msg.obj;
            switch (msg.what){

                case 1:
                    if(currentVolume == -1){
                        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

                        if(currentVolume < 0 ){
                            currentVolume = 0;
                        }
                    }

                    int volume = (int) (percent * maxVolume + currentVolume);
                    if(volume > maxVolume){
                        volume = maxVolume;
                    }else if(volume < 0){
                        volume =0;
                    }

                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volume,0);

                    volume_view.setVisibility(View.VISIBLE);
                    vo_percent.setText((volume * 100) / maxVolume  +"%");


                    break;

                case 2:

                    if(mBrightness  < 0){
                        mBrightness = getWindow().getAttributes().screenBrightness;
                        if(mBrightness <= 0.00f){
                            mBrightness = 0.50f;
                        }
                        if(mBrightness < 0.01f){
                            mBrightness = 0.01f;
                        }
                    }

                    WindowManager.LayoutParams lpa = getWindow().getAttributes();
                    lpa.screenBrightness = mBrightness + percent;
                    if (lpa.screenBrightness > 1.0f)
                        lpa.screenBrightness = 1.0f;
                    else if (lpa.screenBrightness < 0.01f)
                        lpa.screenBrightness = 0.01f;
                    getWindow().setAttributes(lpa);


                    brightness.setVisibility(View.VISIBLE);
                    br_percent.setText((int)(lpa.screenBrightness * 100) +"%");

                    break;
                case 0:
                    endGesture();
                    break;
                default:

                    break;
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG,"onCreate");
        super.onCreate(savedInstanceState);
        //隐藏title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play);
        brightness = findViewById(R.id.brightness);
        volume_view = findViewById(R.id.volume);
        br_percent = findViewById(R.id.br_percent);
        vo_percent = findViewById(R.id.vo_percent);
        mBack = findViewById(R.id.m_back);
        mTitle = findViewById(R.id.m_title);
        mToggle = findViewById(R.id.exo_toggle);

        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        String title = intent.getStringExtra("title");
        Boolean orientation = intent.getBooleanExtra("LOrientation",false);
        if(orientation){
            //横屏
            mLOrientation = true;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }else {
            mLOrientation = false;
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        MyPlayerView playerView = findViewById(R.id.player);
        player = ExoPlayerFactory.newSimpleInstance(this);
        playerView.setPlayer(player);
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this,"Video"));

        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).
                createMediaSource(Uri.parse(data));


     //m3u8
//        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(this,"Video"));
//        dataSourceFactory.getDefaultRequestProperties().set("Referer", "https://avgle.com/video/zr25L5m8q5C/dgl-008-%E9%9B%BB%E6%92%83%E7%A7%BB%E7%B1%8D-%E5%A5%87%E8%B7%A1%E3%81%AE%E7%BE%8E%E5%B7%A8%E4%B9%B3%E7%BE%8E%E5%B0%91%E5%A5%B3%E3%81%AB%E4%B8%80%E6%92%83%E5%A4%A7%E9%87%8F%E9%A1%94%E5%B0%84-%E9%88%B4%E6%9C%A8%E5%BF%83%E6%98%A5");
//        Uri uri = Uri.parse("https://ip78766492.cdn.qooqlevideo.com/key=Dho5QowwW9bDWY+3BS6aYA,s=,end=1566618500,limit=2/data=1566618500/state=cBRS/referer=force,.avgle.com/reftag=56109644/media=hlsA/ssd6/177/1/179844431.m3u8");
//
//        HlsMediaSource videoSource =
//                new HlsMediaSource.Factory(dataSourceFactory)
//                        .setAllowChunklessPreparation(true)
//                        .createMediaSource(uri);

        player.setPlayWhenReady(true);
        player.prepare(videoSource);

        mTitle.setText(title);
        mBack.setOnClickListener(v -> finish());

        mToggle.setOnClickListener(v->{
            if(mLOrientation){
                //横屏切换竖屏
                mLOrientation = false;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }else{
                mLOrientation = true;
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        });

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        playerView.setmHandler(handler);

    }

    @Override
    protected void onDestroy() {
        player.release();
        super.onDestroy();
    }



    /** 手势结束 */
    private void endGesture() {
        currentVolume = -1;
        mBrightness = -1f;

        // 隐藏
        brightness.setVisibility(View.GONE);
        volume_view.setVisibility(View.GONE);

    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        //横竖屏切换的时候会导致activity 被销毁后重新创建，导致onCreate方法重新被执行
        //重写这个方法后会导致onCreate 不会重新执行 https://www.jianshu.com/p/8c40829905ec
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
//横屏
            Log.i(TAG, "onConfigurationChanged: 横屏"+newConfig.orientation);
        }else if (newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
//竖屏
            Log.i(TAG, "onConfigurationChanged: 竖屏"+newConfig.orientation);
        }else if (newConfig.orientation==Configuration.ORIENTATION_UNDEFINED){
//默认
            Log.i(TAG, "onConfigurationChanged: 默认"+newConfig.orientation);
        }

    }


}
