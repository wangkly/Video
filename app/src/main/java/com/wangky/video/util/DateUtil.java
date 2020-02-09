package com.wangky.video.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;



public class DateUtil {

    public static String FormatTimeStamp(long timeStamp){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeStamp);
        String time = sdf.format(cal.getTime());
        return time;
    }



}
