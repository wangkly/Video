package com.wangky.video.listeners;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.wangky.video.MyApplication;
import com.wangky.video.util.Const;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyExceptionHandler implements Thread.UncaughtExceptionHandler {

    private static MyExceptionHandler myExceptionHandler;
    private MyExceptionHandler() {
    }

    public static MyExceptionHandler getInstance(){
        if(myExceptionHandler == null){
            myExceptionHandler =  new MyExceptionHandler();
        }
        return  myExceptionHandler;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {

       SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
       String fileName = sf.format(new Date())+ ".log";
        StringBuffer sb = new StringBuffer();
        try {
            String path = Const.File_SAVE_PATH + "/crash/";
            File dir = new File(path);
            if(!dir.exists()){
                dir.mkdirs();
            }

            FileOutputStream fout  = new FileOutputStream(path + fileName);
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            Throwable cause = e.getCause();
            while (cause != null) {
                cause.printStackTrace(printWriter);
                cause = cause.getCause();
            }
            printWriter.close();
            String result = writer.toString();
            sb.append(result);

            fout.write(sb.toString().getBytes());
            Log.i("MyExceptionHandler", "saveCrashInfo2File: "+sb.toString());
            fout.close();
            Toast.makeText(MyApplication.getInstance(),"捕获crash log"+fileName ,Toast.LENGTH_LONG ).show();
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {

        }


    }


}
