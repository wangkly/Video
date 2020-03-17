package com.wangky.video.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;


public class DateUtil {

    public static String FormatTimeStamp(long timeStamp){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeStamp);
        String time = sdf.format(cal.getTime());
        return time;
    }




    public static String formatTime(long mss) {
        mss = mss - TimeZone.getDefault().getRawOffset();//时区 https://blog.csdn.net/zyp009/article/details/12942041
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");//规定需要形式

        String TotalTime = simpleDateFormat.format(mss);//转化为需要形式

        return TotalTime;

    }



}
