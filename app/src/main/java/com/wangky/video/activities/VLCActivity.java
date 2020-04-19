/*****************************************************************************
 * JavaActivity.java
 *****************************************************************************
 * Copyright (C) 2016-2019 VideoLAN
 * All rights reserved.
 *
 * This software may be modified and distributed under the terms
 * of the BSD license. See the LICENSE file for details.
 *****************************************************************************/

package com.wangky.video.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.wangky.video.MyPlayerView;
import com.wangky.video.R;
import com.wangky.video.listeners.UserOperationListener;
import com.wangky.video.util.DateUtil;
import com.wangky.video.view.OperationDialogFragment;
import com.wangky.video.vlc.VLCVideoLayout;
import com.wangky.video.vlc.VideoHelper;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;

import static org.videolan.libvlc.MediaPlayer.Event.TimeChanged;

public class VLCActivity extends AppCompatActivity implements UserOperationListener,View.OnClickListener ,OperationDialogFragment.OnOperationListener{
    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;

/****************************************/
    private float mBrightness = -1f;
    private AudioManager audioManager;
    private int maxVolume = 0;
    private int currentVolume = -1;
    private int progressChange = -1;
    private long current = 0;//视频播放当前进度
    private long duration = 0;//视频时长
    private LinearLayout brightness;
    private LinearLayout volume_view;
    private LinearLayout progress_tip;
    private ImageButton progress_icon;
    private TextView br_percent;
    private TextView vo_percent;
    private TextView progress_percent;
    private ProgressBar mLoading;
    private Boolean mUpdateTime = false;
    private ImageButton mBack;
    private TextView mTitle;
    private ImageButton mToggle;
    private TextView mCurrentTime;//用于显示当前时间
    private ImageButton mMoreOperation;//更多操作

    private FrameLayout mVlcController;
    private ImageButton mVlcPlay;
    private ImageButton mVlcPause;

    private TextView mVlcPosition;
    private TextView mVlcDuration;
    private SeekBar mProgress;


    //是否横屏播放
    private Boolean mLOrientation =false ;
 /****************************************/

    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;

    private VLCVideoLayout mVideoLayout = null;

    private String vPath;

    // Video tools
    private VideoHelper mVideoHelper = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_vlc);

        Intent intent = getIntent();
        vPath = intent.getStringExtra("data");
        String title = intent.getStringExtra("title");

        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        mLibVLC = new LibVLC(this, args);
        mMediaPlayer = new MediaPlayer(mLibVLC);

        mVideoLayout = findViewById(R.id.video_layout);
        mVideoLayout.setOptListener(this);

/******************************************************/
        brightness = findViewById(R.id.brightness);
        volume_view = findViewById(R.id.volume);
        progress_tip = findViewById(R.id.progress_tip);
        progress_icon = findViewById(R.id.progress_icon);
        br_percent = findViewById(R.id.br_percent);
        vo_percent = findViewById(R.id.vo_percent);
        progress_percent = findViewById(R.id.progress_percent);
        mBack = findViewById(R.id.m_back);
        mTitle = findViewById(R.id.m_title);
        mToggle = findViewById(R.id.exo_toggle);
        mLoading = findViewById(R.id.loading);
        mCurrentTime = findViewById(R.id.current_time);
        mMoreOperation = findViewById(R.id.more_operation);

        mVlcController = findViewById(R.id.vlc_controller);
        mVlcPlay = findViewById(R.id.vlc_play);
        mVlcPause = findViewById(R.id.vlc_pause);
        mVlcPosition = findViewById(R.id.vlc_position);
        mVlcDuration = findViewById(R.id.vlc_duration);
        mProgress = findViewById(R.id.vlc_progress);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //启动一个线程，更新显示当前时间
        mUpdateTime = true;

        mTitle.setText(title);
        mBack.setOnClickListener(this);
        mToggle.setOnClickListener(this);
        mVlcPlay.setOnClickListener(this);
        mVlcPause.setOnClickListener(this);

        mMoreOperation.setOnClickListener(this);

        this.updateCurrentTime();
    }


    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMediaPlayer.release();
        mLibVLC.release();
    }

    @Override
    protected void onStart() {
        super.onStart();

        this.attachViews(mMediaPlayer,mVideoLayout, ENABLE_SUBTITLES, USE_TEXTURE_VIEW);

        try {
            Media media = new Media(mLibVLC, vPath);
            mMediaPlayer.setMedia(media);
            media.release();
        } catch (Exception e) {
            throw new RuntimeException("Invalid asset folder");
        }
        mMediaPlayer.play();

        initPlayerStatus();
    }

    public void initPlayerStatus(){
        mVlcPlay.setVisibility(View.GONE);
        mVlcPosition.setText("00:00");

        mMediaPlayer.setEventListener(event -> {
           if(event.type == TimeChanged){
               runOnUiThread(()->{
                   mVlcPosition.setText(DateUtil.formatTime(mMediaPlayer.getTime()));
                   mProgress.setProgress((int) mMediaPlayer.getTime());
               });
           }
        });

        new Handler().postDelayed(() -> {
            mVlcDuration.setText(DateUtil.formatTime(mMediaPlayer.getLength()));
            mProgress.setMax((int) mMediaPlayer.getLength());

        },1000);

        mProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mMediaPlayer.setTime(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

    }



    @Override
    protected void onStop() {
        super.onStop();

        mMediaPlayer.stop();
        this.detachViews();
    }


    /**
     * Attach a video layout to the player
     *
     * @param surfaceFrame {@link VLCVideoLayout} in which the video will be displayed
     * @param subtitles Whether you wish to show subtitles
     * @param textureView If true, {@link VLCVideoLayout} will use a {@link android.view.TextureView} instead of a {@link android.view.SurfaceView}
     */
    public void attachViews(MediaPlayer player,@NonNull VLCVideoLayout surfaceFrame, boolean subtitles, boolean textureView) {
        mVideoHelper = new VideoHelper(player, surfaceFrame, subtitles, textureView);
        mVideoHelper.attachViews();

    }

    /**
     * Detach the video layout
     */
    public void detachViews() {
        if (mVideoHelper != null) {
            mVideoHelper.release();
            mVideoHelper = null;
        }
    }

    /**
     * Update the video surfaces, either to switch from one to another or to resize it
     */
    public void updateVideoSurfaces() {
        if (mVideoHelper != null) mVideoHelper.updateVideoSurfaces();
    }

    /**
     * Set the video scale type, by default, scaletype is set to ScaleType.SURFACE_BEST_FIT
     * @param {@link ScaleType} to rule the video surface filling
     */
    public void setVideoScale(@NonNull VideoHelper.ScaleType type) {
        if (mVideoHelper != null) mVideoHelper.setVideoScale(type);
    }

    /**
     * Get the current video scale type
     * @return the current {@link VideoHelper.ScaleType} used by MediaPlayer
     */
    @NonNull
    public VideoHelper.ScaleType getVideoScale() {
        return mVideoHelper != null ? mVideoHelper.getVideoScale() : VideoHelper.ScaleType.SURFACE_BEST_FIT;
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.m_back:
                   this.finish();
                break;

            case R.id.vlc_play:
                if(!mMediaPlayer.isPlaying()){
                    mMediaPlayer.play();
                    mVlcPause.setVisibility(View.VISIBLE);
                    mVlcPlay.setVisibility(View.GONE);
                }

                break;

            case R.id.vlc_pause:
                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.pause();
                    mVlcPause.setVisibility(View.GONE);
                    mVlcPlay.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.exo_toggle:
                if(mLOrientation){
                    //横屏切换竖屏
                    mLOrientation = false;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }else{
                    mLOrientation = true;
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
                break;

            case R.id.more_operation:
                OperationDialogFragment fragment = new OperationDialogFragment();
                float speed = mMediaPlayer.getRate();
                int resizeMode = 0;
                VideoHelper.ScaleType scale = getVideoScale();
                if(scale.equals(VideoHelper.ScaleType.SURFACE_FIT_SCREEN)){
                    resizeMode = 4;
                }else if(scale.equals(VideoHelper.ScaleType.SURFACE_FILL)){
                    resizeMode = 3;
                }else if(scale.equals(VideoHelper.ScaleType.SURFACE_ORIGINAL)){
                    resizeMode = 1;
                }
                Bundle bundle = new Bundle();
                bundle.putSerializable("orientation",mLOrientation);
                bundle.putSerializable("speed",speed);
                bundle.putSerializable("resizeMode",resizeMode);
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(),"operationFragment");

                break;

            default:

                break;

        }

    }





    /** 手势结束 */
    private void endGesture() {
        currentVolume = -1;
        mBrightness = -1f;
        progressChange = -1;
        current = 0;
        // 隐藏
        brightness.setVisibility(View.GONE);
        volume_view.setVisibility(View.GONE);
        progress_tip.setVisibility(View.GONE);

        new Handler().postDelayed(() -> mVlcController.setVisibility(View.GONE),2000);

    }


    @Override
    public void onVideoVolumeChange(float percent) {

        if(currentVolume < 0){
            volume_view.setVisibility(View.VISIBLE);
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

        vo_percent.setText((volume * 100) / maxVolume  +"%");

    }

    @Override
    public void onViewBrightnessChange(float percent) {

        if(mBrightness  < 0){
            mBrightness = getWindow().getAttributes().screenBrightness;
            if(mBrightness <= 0.00f){
                mBrightness = 0.50f;
            }
            if(mBrightness < 0.01f){
                mBrightness = 0.01f;
            }
            brightness.setVisibility(View.VISIBLE);
        }
        WindowManager.LayoutParams lpa = getWindow().getAttributes();
        lpa.screenBrightness = mBrightness + percent;
        if (lpa.screenBrightness > 1.0f)
            lpa.screenBrightness = 1.0f;
        else if (lpa.screenBrightness < 0.01f)
            lpa.screenBrightness = 0.01f;
        getWindow().setAttributes(lpa);
        br_percent.setText((int)(lpa.screenBrightness * 100) +"%");

    }

    @Override
    public void onVideoProgressChange(int type, float percent) {

        if(progressChange == -1){
            current =  mMediaPlayer.getTime();
            progress_tip.setVisibility(View.VISIBLE);
            progressChange = 1;
        }
        if(duration == 0){
            duration =  mMediaPlayer.getLength();
        }
        //每次快进快退 按30秒范围内
        long progress = (long) (30*1000* percent);

        if(type == MyPlayerView.PROGRESS_FORWARD){

            current += progress;
            if(current >= duration){
                current =duration;
            }
            progress_percent.setText(formatProgress(current,duration));
            mMediaPlayer.setTime(current);
            progress_icon.setImageResource(R.drawable.ic_forward);
            mMediaPlayer.play();
        }else if(type == MyPlayerView.PROGRESS_BACKWARD){

            current -= progress;
            if(current <= 0){
                current =0;
            }
            mMediaPlayer.setTime(current);
            mMediaPlayer.play();
            progress_percent.setText(formatProgress(current,duration));
            progress_icon.setImageResource(R.drawable.ic_backward);
        }

    }

    @Override
    public void onOperationEnd() {
        this.endGesture();
    }


    @Override
    public void onOperationStart() {
        mVlcController.setVisibility(View.VISIBLE);
    }

    public String formatProgress(long current, long duration){
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(current);
        String total = formatter.format(duration);
        return hms +"/"+total;
    }

    public void updateCurrentTime(){
        new Thread(() -> {
            while (mUpdateTime){
                //延迟两秒
                try {
                    Thread.sleep( 1000 );
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                long currentTimestamp = System.currentTimeMillis();
                SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
                String time = formatter.format(currentTimestamp);


                runOnUiThread(() -> mCurrentTime.setText(time));
            }
        }).start();
    }


    /**
     * OperationDialogFragment.OnOperationListener
     * @param speed
     */
    @Override
    public void onPlaySpeedChange(float speed) {
        mMediaPlayer.setRate(speed);
    }

    @Override
    public void onViewScaleChange(int scale) {
        if(scale == 0){
            setVideoScale(VideoHelper.ScaleType.SURFACE_BEST_FIT);
        }else if(scale == 4){//裁剪
            setVideoScale(VideoHelper.ScaleType.SURFACE_FIT_SCREEN);
        }else if(scale == 3){//拉伸
            setVideoScale(VideoHelper.ScaleType.SURFACE_FILL);
        }else {
            setVideoScale(VideoHelper.ScaleType.SURFACE_ORIGINAL);
        }

    }
}
