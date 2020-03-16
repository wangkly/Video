package com.wangky.video.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateUtil {

    public static String FormatTimeStamp(long timeStamp){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeStamp);
        String time = sdf.format(cal.getTime());
        return time;
    }


    /**
     * 将long值转换为以小时计算的格式
     * @param mss
     * @return
     */
    public static String formatLongTime(long mss) {
        String DateTimes = null;
        long hours = (mss % ( 60 * 60 * 24)) / (60 * 60);
        long minutes = (mss % ( 60 * 60)) /60;
        long seconds = mss % 60;

        DateTimes=String.format("%02d:", hours)+ String.format("%02d:", minutes) + String.format("%02d", seconds);
        String.format("%2d:", hours);
        return DateTimes;
    }


    public static String formatTime(long length) {

        Date date = new Date(length);//调用Date方法获值

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");//规定需要形式

        String TotalTime = simpleDateFormat.format(date);//转化为需要形式

        return TotalTime;

    }



}
