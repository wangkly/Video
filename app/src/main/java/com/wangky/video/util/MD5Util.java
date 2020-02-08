package com.wangky.video.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {


    public static String md5Encode32(String content){
        byte[] hash = new byte[0];

        try {
            hash =  MessageDigest.getInstance("MD5").digest(content.getBytes("UTF-8"));

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //对生成的16字节数组进行补零操作
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10){
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();


    }
}
