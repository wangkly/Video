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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.wangky.video.R;

import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;

public class VLCActivity extends AppCompatActivity  implements IVLCVout.OnNewVideoLayoutListener{
    private static final boolean USE_TEXTURE_VIEW = false;
    private static final boolean ENABLE_SUBTITLES = true;
    private static final String ASSET_FILENAME = "bbb.m4v";


    private LibVLC mLibVLC = null;
    private MediaPlayer mMediaPlayer = null;

    private SurfaceView mVideoSurface = null;

    private String vPath;
    private FrameLayout mVideoSurfaceFrame = null;
    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vlc);

        Intent intent = getIntent();
        vPath = intent.getStringExtra("data");

        final ArrayList<String> args = new ArrayList<>();
        args.add("-vvv");
        mLibVLC = new LibVLC(this, args);
        mMediaPlayer = new MediaPlayer(mLibVLC);
        mVideoSurface = (SurfaceView) findViewById(R.id.video_surface);
        mVideoSurfaceFrame = (FrameLayout) findViewById(R.id.video_surface_frame);

        mVideoSurfaceFrame.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            private final Runnable mRunnable = new Runnable() {
                @Override
                public void run() {
                    updateVideoSurfaces();
                }
            };
            @Override
            public void onLayoutChange(View v, int left, int top, int right,
                                       int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (left != oldLeft || top != oldTop || right != oldRight || bottom != oldBottom) {
                    mHandler.removeCallbacks(mRunnable);
                    mHandler.post(mRunnable);
                }

        }
        });

    }

    private void updateVideoSurfaces() {

        int sw = getWindow().getDecorView().getWidth();
        int sh = getWindow().getDecorView().getHeight();

        // sanity check
        if (sw * sh == 0) {

            return;
        }

        mMediaPlayer.getVLCVout().setWindowSize(sw, sh);
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

        final IVLCVout vlcVout = mMediaPlayer.getVLCVout();
        vlcVout.setVideoView(mVideoSurface);
        vlcVout.attachViews(this);

//        vlcVout.setVideoSurface(mVideoSurface.getHolder().getSurface(),mVideoSurface.getHolder());
//        vlcVout.attachViews();
//        vlcVout.setWindowSize(mVideoSurface.getWidth(), mVideoSurface.getHeight());

        Media media = new Media(mLibVLC, vPath);
        mMediaPlayer.setMedia(media);
        media.release();
        mMediaPlayer.play();


    }

    @Override
    protected void onStop() {
        super.onStop();

        mMediaPlayer.stop();
        mMediaPlayer.getVLCVout().detachViews();
    }

    @Override
    public void onNewVideoLayout(IVLCVout ivlcVout, int i, int i1, int i2, int i3, int i4, int i5) {

    }
}
