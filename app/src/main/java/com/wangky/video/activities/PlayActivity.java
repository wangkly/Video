package com.wangky.video.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SeekParameters;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.wangky.video.MyPlayerView;
import com.wangky.video.R;
import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.enums.MessageType;
import com.wangky.video.event.TaskEvent;
import com.wangky.video.listeners.UserOperationListener;
import com.wangky.video.util.Const;
import com.wangky.video.util.FileUtils;
import com.wangky.video.view.MetaDialogFragment;
import com.wangky.video.view.OperationDialogFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.TimeZone;

import wseemann.media.FFmpegMediaMetadataRetriever;

import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL;
import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT;
import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT;
import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH;
import static com.google.android.exoplayer2.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM;

public class PlayActivity extends AppCompatActivity implements UserOperationListener,OperationDialogFragment.OnOperationListener{

    private final String TAG = "PlayActivity.class";

    private DataSource.Factory mDataSourceFactory;
    private ExoPlayer player;
    private ImageButton mBack;
    private TextView mTitle;
    private ImageButton mToggle;
    private ImageButton mShowInfo;
    private ImageButton mVlc;
    private TextView mCurrentSpeed;//用于显示当前时间
    private TextView mCurrentTime;//用于显示当前时间
    private ImageButton mMoreOperation;//更多操作
    //是否横屏播放
    private Boolean mLOrientation =false ;

    private float mBrightness = -1f;
    private AudioManager audioManager;
    private int maxVolume = 0;
    private int currentVolume = -1;
    private int progressChange = -1;
    private long current = 0;//视频播放当前进度

    private long duration = 0;//视频时长

    private MyPlayerView playerView;

    private LinearLayout brightness;

    private LinearLayout volume_view;

    private LinearLayout progress_tip;

    private ImageButton progress_icon;

    private TextView br_percent;

    private TextView vo_percent;

    private TextView progress_percent;

    private ProgressBar mLoading;

    private PlayerEventListener eventListener;

    private Boolean mUpdateTime = false;
    private Boolean mUpdateSpeed = false;

    private String vPath;
    private String vTitle;

    private long taskId;
    private String hash;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_play);
        //播放器事件监听
        eventListener = new PlayerEventListener();

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
        mShowInfo = findViewById(R.id.exo_showInfo);
        mVlc = findViewById(R.id.exo_vlc);
        mLoading = findViewById(R.id.loading);
        mCurrentTime = findViewById(R.id.current_time);
        mCurrentSpeed = findViewById(R.id.current_speed);
        mMoreOperation = findViewById(R.id.more_operation);
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        String title = intent.getStringExtra("title");
        taskId = intent.getLongExtra("taskId",0);
        hash = intent.getStringExtra("hash");
        vPath = data;
        vTitle = title;
        playerView = findViewById(R.id.player);
        player = new ExoPlayer.Builder(PlayActivity.this).build();;
        player.setSeekParameters(SeekParameters.NEXT_SYNC);
        playerView.setPlayer(player);
        mDataSourceFactory = new DefaultDataSource.Factory(this);
        MediaItem item = MediaItem.fromUri(Uri.parse(data));
        MediaSource videoSource = new ProgressiveMediaSource.Factory(mDataSourceFactory).
                createMediaSource(item);


     //m3u8
//        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(this,"Video"));
//        dataSourceFactory.getDefaultRequestProperties().set("Referer", "https://avgle.com/video/zr25L5m8q5C/dgl-008-%E9%9B%BB%E6%92%83%E7%A7%BB%E7%B1%8D-%E5%A5%87%E8%B7%A1%E3%81%AE%E7%BE%8E%E5%B7%A8%E4%B9%B3%E7%BE%8E%E5%B0%91%E5%A5%B3%E3%81%AB%E4%B8%80%E6%92%83%E5%A4%A7%E9%87%8F%E9%A1%94%E5%B0%84-%E9%88%B4%E6%9C%A8%E5%BF%83%E6%98%A5");
//        Uri uri = Uri.parse("https://ip78766492.cdn.qooqlevideo.com/key=Dho5QowwW9bDWY+3BS6aYA,s=,end=1566618500,limit=2/data=1566618500/state=cBRS/referer=force,.avgle.com/reftag=56109644/media=hlsA/ssd6/177/1/179844431.m3u8");
//
//        HlsMediaSource videoSource =
//                new HlsMediaSource.Factory(dataSourceFactory)
//                        .setAllowChunklessPreparation(true)
//                        .createMediaSource(uri);

        player.setMediaSource(videoSource);
        player.prepare();
        player.setPlayWhenReady(true);
        player.addListener(eventListener);


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

        mShowInfo.setOnClickListener(v->{
            FFmpegMediaMetadataRetriever retriever = new  FFmpegMediaMetadataRetriever();
            retriever.setDataSource(vPath);
            try {
                String frameRate = retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FRAMERATE);
                String fileSize =  retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_FILESIZE);
                String bitRate =  retriever.extractMetadata(FFmpegMediaMetadataRetriever.METADATA_KEY_VARIANT_BITRATE);
                MetaDialogFragment fragment = new MetaDialogFragment();
                retriever.release();
                MediaMetadataRetriever metaRetriever = new MediaMetadataRetriever();
                metaRetriever.setDataSource(vPath);
                String height = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                String width = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                bitRate = metaRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE);
                metaRetriever.release();
                Bundle bundle = new Bundle();
                bundle.putSerializable("orientation",mLOrientation);
                bundle.putSerializable("frameRate",frameRate);
                bundle.putSerializable("fileName",title);
                bundle.putSerializable("fileSize",FileUtils.getFileSize(Long.valueOf(fileSize)));
                bundle.putSerializable("bitRate",FileUtils.downloadSpeed(Long.valueOf(bitRate)));
                bundle.putSerializable("resolution",width+"*"+height);
                fragment.setArguments(bundle);
                fragment.show(getSupportFragmentManager(),"metaFragment");
            }catch (Exception e){
                e.printStackTrace();
            }
        });


        mMoreOperation.setOnClickListener(v -> {
            OperationDialogFragment fragment = new OperationDialogFragment();
            float speed = player.getPlaybackParameters().speed;
            int resizeMode = playerView.getResizeMode();
            Bundle bundle = new Bundle();
            bundle.putSerializable("orientation",mLOrientation);
            bundle.putSerializable("speed",speed);
            bundle.putSerializable("resizeMode",resizeMode);
            fragment.setArguments(bundle);
            fragment.show(getSupportFragmentManager(),"operationFragment");

        });

        mVlc.setOnClickListener(v->{
            Intent vlcIntent = new Intent(PlayActivity.this, VLCActivity.class);
            vlcIntent.putExtra("LOrientation",false);
            vlcIntent.putExtra("data",vPath);
            vlcIntent.putExtra("title",vTitle);
            startActivity(vlcIntent);
            finish();
        });

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        playerView.setUserOperationListener(this);

        //启动一个线程，更新显示当前时间
        mUpdateTime = true;
        this.updateCurrentTime();

    }



    @Override
    protected void onDestroy() {
        mUpdateTime = false;
        mUpdateSpeed =false;
        player.release();
        super.onDestroy();
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


    class PlayerEventListener implements Player.Listener{

        @Override
        public void onIsLoadingChanged(boolean isLoading) {
            Log.i(TAG,"onLoadingChanged===> " +isLoading);
//            if(isLoading){
//                mLoading.setVisibility(View.VISIBLE);
//            }else{
//                mLoading.setVisibility(View.GONE);
//            }
        }

        /**
         * @param playbackState
         * com.google.android.exoplayer2.Player#STATE_IDLE  空闲 1
         * com.google.android.exoplayer2.Player#STATE_BUFFERING 缓冲中 2
         * com.google.android.exoplayer2.Player#STATE_READY ready to play 3
         * com.google.android.exoplayer2.Player#STATE_ENDED 播放完毕 4
         *
         * In addition to these states, the player has a playWhenReady flag to indicate the user intention to play.
         * The player is only playing if the state is Player.STATE_READY and playWhenReady=true
         */
        @Override
        public void onPlaybackStateChanged(int playbackState) {
            Log.i(TAG,"onPlayerStateChanged===> "+ playbackState);

            if(playbackState == Player.STATE_BUFFERING){
                mLoading.setVisibility(View.VISIBLE);
                playerView.showController();
            }else if(playbackState == Player.STATE_READY){
                mLoading.setVisibility(View.GONE);
                playerView.hideController();
//                player.play();
            }


//            if(playWhenReady && playbackState == Player.STATE_READY){
//                    mLoading.setVisibility(View.GONE);
//            }else if (playWhenReady) {
//                // Not playing because playback ended, the player is buffering, stopped or
//                // failed. Check playbackState and player.getPlaybackError for details.
//                if(playbackState == Player.STATE_BUFFERING){
//                    if(progressChange == -1){
//                       mLoading.setVisibility(View.VISIBLE);
//                    }
//                }else if(playbackState == Player.STATE_ENDED){
////                    Toast.makeText(PlayActivity.this,"播放结束",Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                // Paused by app.
//                //貌似暂停时 playWhenReady ==false
//                Toast.makeText(PlayActivity.this,"暂停",Toast.LENGTH_SHORT).show();
//            }

        }

        @Override
        public void onPlayerError(PlaybackException error) {
            Log.i(TAG,"error===>"+error.getMessage());
            Toast.makeText(PlayActivity.this,"出错了。。。",Toast.LENGTH_SHORT).show();

            long current = player.getCurrentPosition();
            MediaItem item = MediaItem.fromUri(Uri.parse(vPath));
            MediaSource videoSource = new ProgressiveMediaSource.Factory(mDataSourceFactory).
                    createMediaSource(item);
            player.setPlayWhenReady(true);
            player.setMediaSource(videoSource);
            player.seekTo(current);
        }
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
    public void onVideoProgressChange(int type ,float percent) {
//        percent = percent / 100;//再缩小100倍
//        Log.e(TAG,"onVideoProgressChange===> "+ type);
        if(progressChange == -1){
            current = player.getCurrentPosition();
            progress_tip.setVisibility(View.VISIBLE);
            progressChange = 1;
        }
        if(duration == 0){
            duration =  player.getDuration();
        }
        //每次快进快退 按30秒范围内
        long progress = (long) (30*1000* percent);

        if(type == MyPlayerView.PROGRESS_FORWARD){

           current += progress;
           if(current >= duration){
               current =duration;
           }
           progress_percent.setText(formatProgress(current,duration));
           player.seekTo(current);
           progress_icon.setImageResource(R.drawable.ic_forward);
        }else if(type == MyPlayerView.PROGRESS_BACKWARD){

            current -= progress;
            if(current <= 0){
                current =0;
            }
            player.seekTo(current);
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

    }

    @Override
    public void onViewTap() {
        if(mLoading.getVisibility() != View.VISIBLE){
            playerView.performClick();
        }
    }


    @Override
    public void onViewDoubleTap() {
       if(player.isPlaying()){
           player.pause();
       }else {
           player.play();
       }
       playerView.performClick();
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


    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    /***
     * 更新下载进度
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void updateDownloadTaskUI(TaskEvent event){
        if(event.getMessage().equals(MessageType.UPDATE_UI)){
            List<DownloadTaskEntity> tasks = event.getTasks();
            for (DownloadTaskEntity entity:tasks){
                if(entity.getHash().equalsIgnoreCase(hash)){
                    if(entity.getmTaskStatus() != Const.DOWNLOAD_SUCCESS){
                       String speed =  FileUtils.downloadSpeed(entity.getmDownloadSpeed());
                        Log.i("PlayActivity","更新下载速度" + speed);
                        mCurrentSpeed.setVisibility(View.VISIBLE);
                        runOnUiThread(() -> mCurrentSpeed.setText(speed));
                        Log.i("mTaskStatus ==>",String.valueOf(entity.getmTaskStatus()));
                    }
                }
            }

        }
    }


    /**
     * 设置播放速度
     * com.wangky.video.view.OperationDialogFragment.OnOperationListener#onPlaySpeedChange(float)
     * @param speed 播放速度
     */
    @Override
    public void onPlaySpeedChange(float speed) {
        this.setPlaybackSpeed(speed);
    }

    @Override
    public void onViewScaleChange(int scale) {
        this.setVideoScalingMode(scale);
    }


    /**
     * 调整播放速度 倍速
     */
    public void setPlaybackSpeed(float speed){
        PlaybackParameters parameters = new PlaybackParameters(speed);
        if(null != player){
            player.setPlaybackParameters(parameters);
        }
    }


    /**
     * 设置播放画面 缩放大小
     *
     */
    public void setVideoScalingMode(int scale){
        if(null != playerView){
            switch (scale){
                case 0:
                    playerView.setResizeMode(RESIZE_MODE_FIT);
                    break;
                case 1:
                    if(mLOrientation){//横屏
                        playerView.setResizeMode(RESIZE_MODE_FIXED_WIDTH);
                    }else {
                        playerView.setResizeMode(RESIZE_MODE_FIXED_HEIGHT);
                    }
                    break;
                case 2:
                    playerView.setResizeMode(RESIZE_MODE_FIXED_HEIGHT);
                    break;

                case 3:
                    playerView.setResizeMode(RESIZE_MODE_FILL);
                    break;
                case 4:
                    playerView.setResizeMode(RESIZE_MODE_ZOOM);
                    break;

                default:
                    playerView.setResizeMode(RESIZE_MODE_FIT);
                    break;
            }
        }
    }



}
