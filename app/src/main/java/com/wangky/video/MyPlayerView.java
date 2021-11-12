package com.wangky.video;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.google.android.exoplayer2.ui.PlayerView;
import com.wangky.video.listeners.UserOperationListener;
import com.wangky.video.util.DensityUtil;

import androidx.core.view.GestureDetectorCompat;

public class MyPlayerView extends PlayerView {

    private GestureDetectorCompat mGestureDetector;

    private int windowWidth;

    private int windowHeight;

    private float STEP_PROGRESS = 10;
    private Boolean firstScroll =false;

    private  int GESTURE_FLAG = 1;

    private final int GESTURE_MODIFY_VOLUME = 1;

    private final int GESTURE_MODIFY_BRIGHT = 2;

    private final int GESTURE_MODIFY_PROGRESS = 3;

    private UserOperationListener mOptListener;
    //快进
    public static final int  PROGRESS_FORWARD =1;
    //快退
    public static final  int PROGRESS_BACKWARD = 2;

    public static final long PROGRESS_STEP = 3000;//毫秒

    private GestureDetector.SimpleOnGestureListener myGestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d("-------->playing","scroll");
            //distanceX e1.getX() - e2.getX(); 正数 向右滑动 负数向左滑动
            //distanceY e1.getY() - e2.getY(); 正数 从上往下滑动 负数从下往上滑动
            float oldX = e1.getX();
            float oldY = e1.getY();
            float y =  e2.getRawY();
            float x = e2.getRawX();
            if(firstScroll){
                if(!MyPlayerView.super.isControllerVisible()){
                    MyPlayerView.super.showController();
                }
                // 以触摸屏幕后第一次滑动为标准，避免在屏幕上操作切换混乱
                // 横向的距离变化大则调整进度，纵向的变化大则调整音量、亮度
                if(Math.abs(distanceX) >= Math.abs(distanceY)){
                    GESTURE_FLAG = GESTURE_MODIFY_PROGRESS;
                }else{
                    if(oldX > windowWidth * 3.0 /5){
                        Log.d("-------->scroll","在屏幕右侧滑动");
                        GESTURE_FLAG = GESTURE_MODIFY_VOLUME;
                    }else if(oldX < windowWidth *2.0 /5) {
                        Log.d("-------->scroll","在屏幕左侧滑动");
                        GESTURE_FLAG = GESTURE_MODIFY_BRIGHT;
                    }
                }
            }

            if(GESTURE_FLAG == GESTURE_MODIFY_PROGRESS){
                if(Math.abs(distanceX) > Math.abs(distanceY)){
                    if(distanceX >= DensityUtil.dip2px(getContext(),STEP_PROGRESS) ){ //快退
                        float percent = Math.abs((oldX - x) / windowWidth);
                        mOptListener.onVideoProgressChange(PROGRESS_BACKWARD,percent);
                    }else if(distanceX <= - DensityUtil.dip2px(getContext(),STEP_PROGRESS)){//快进
                        float percent = Math.abs((oldX - x) / windowWidth);
                        mOptListener.onVideoProgressChange(PROGRESS_FORWARD,percent);
                    }
                }
            }else if(GESTURE_FLAG == GESTURE_MODIFY_VOLUME){
                mOptListener.onVideoVolumeChange((oldY - y) / windowHeight);
            }else if (GESTURE_FLAG == GESTURE_MODIFY_BRIGHT){
                mOptListener.onViewBrightnessChange((oldY - y) / windowHeight );
            }
            firstScroll = false;// 第一次scroll执行完成，修改标志
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("-------->playing","onDoubleTap");
            mOptListener.onViewDoubleTap();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.d("-------->playing","onSingleTapUp");
//            MyPlayerView.super.performClick();
            mOptListener.onViewTap();
            return super.onSingleTapUp(e);
        }


        @Override
        public boolean onDown(MotionEvent e) {
            Log.d("-------->playing","onDown");
            firstScroll = true;
            return true;
        }
    };


    public MyPlayerView(Context context) {
        super(context);
        this.init();
    }

    public MyPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init();
    }

    public MyPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.init();
    }


    public void init(){
        mGestureDetector = new GestureDetectorCompat(getContext(),myGestureListener);
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dm);
        windowWidth = dm.widthPixels;
        windowHeight = dm.heightPixels;
    }



    public void setUserOperationListener(UserOperationListener listener){
        this.mOptListener =listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(mGestureDetector.onTouchEvent(event)) return true;

        // 处理手势结束
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:

                mOptListener.onOperationEnd();
                break;
        }

        return super.onTouchEvent(event);
    }









}
