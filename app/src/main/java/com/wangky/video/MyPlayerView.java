package com.wangky.video;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.WindowManager;

import com.google.android.exoplayer2.ui.PlayerView;

import androidx.core.view.GestureDetectorCompat;

public class MyPlayerView extends PlayerView {

    private GestureDetectorCompat mGestureDetector;


    private int windowWidth;

    private int windowHeight;


    private Handler mHandler;

    private Boolean firstScroll =false;

    private  int GESTURE_FLAG = 1;

    private final int GESTURE_MODIFY_VOLUME = 1;

    private final int GESTURE_MODIFY_BRIGHT = 2;

    private final int GESTURE_MODIFY_PROGRESS = 3;


    private GestureDetector.SimpleOnGestureListener myGestureListener = new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            Log.d("-------->playing","scroll");
            //distanceX e1.getX() - e2.getX(); 正数 向右滑动 负数向左滑动
            //distanceY e1.getY() - e2.getY(); 正数 从上往下滑动 负数从下往上滑动

            float oldX = e1.getX();
            float oldY = e1.getY();

            float y =  e2.getRawY();

            if(firstScroll){
                if(oldX > windowWidth * 3.0 /5){
                    Log.d("-------->scroll","在屏幕右侧滑动");
                    Log.d("-------->垂直方向滑动距离"," ==>"+distanceY);
                    GESTURE_FLAG = GESTURE_MODIFY_VOLUME;
                }else if(oldX < windowWidth *2.0 /5) {
                    Log.d("-------->scroll","在屏幕左侧滑动");
                    Log.d("-------->垂直方向滑动距离"," ==>"+distanceY);
                    GESTURE_FLAG = GESTURE_MODIFY_BRIGHT;
                }
            }



            if(GESTURE_FLAG == GESTURE_MODIFY_VOLUME){
                managerVolume((oldY - y) / windowHeight);
            }else if (GESTURE_FLAG == GESTURE_MODIFY_BRIGHT){
                managerBrightness((oldY - y) / windowHeight );
            }

            firstScroll = false;// 第一次scroll执行完成，修改标志

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

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

            Log.d("-------->playing","onDown");

            firstScroll = true;

            return MyPlayerView.super.performClick();
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
//        DisplayMetrics dm = new DisplayMetrics();
//        windowManager.getDefaultDisplay().getMetrics(dm);
//        dm.widthPixels;

        Display disp =  windowManager.getDefaultDisplay();
         windowWidth = disp.getWidth();
         windowHeight = disp.getHeight();

    }


    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(mGestureDetector.onTouchEvent(event)) return true;

        // 处理手势结束
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:

                Message msg = new Message();
                msg.obj = 0.0f;
                msg.what = 0;
                mHandler.sendMessage(msg);

                break;
        }

        return super.onTouchEvent(event);
    }


    /**
     * 调节音量
     */
    public void managerVolume(float percent){
        Message msg = new Message();
        msg.obj = percent;
        msg.what = 1;
        mHandler.sendMessage(msg);
    }


    public void managerBrightness(float percent){

        Message msg = new Message();
        msg.obj = percent;
        msg.what = 2;
        mHandler.sendMessage(msg);
    }




}
