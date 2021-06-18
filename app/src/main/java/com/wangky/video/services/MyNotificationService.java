package com.wangky.video.services;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.wangky.video.R;
import com.wangky.video.activities.DownloadActivity;
import com.wangky.video.beans.DownloadTaskEntity;
import com.wangky.video.enums.MessageType;
import com.wangky.video.event.TaskEvent;
import com.wangky.video.util.FileUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class MyNotificationService extends Service {

    private NotificationManager nManager;

    public MyNotificationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onCreate() {
        nManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        super.onCreate();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * Notification在android 8.0以上设置时，需要设置渠道信息才能够正常显示通知
     * @param event
     */
    @TargetApi(Build.VERSION_CODES.O)
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMessage(TaskEvent event){
        MessageType message = event.getMessage();
        List<DownloadTaskEntity> tasks = event.getTasks();
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        for(DownloadTaskEntity task :tasks){
            String notStr = "";
            notStr += task.getmFileName();
            notStr += " - " + FileUtils.downloadSpeed(task.getmDownloadSpeed());
            inboxStyle.addLine(notStr);
        }

        String channelId = "my_channel_01";
        NotificationChannel mChannel = new NotificationChannel(channelId, "my_channel", NotificationManager.IMPORTANCE_LOW);
        nManager.createNotificationChannel(mChannel);

        Intent intent = new Intent(this, DownloadActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,intent,0);
        //setStyle 貌似在android6.0不起作用？？？
        Notification notification = new NotificationCompat
                .Builder(getApplicationContext(),channelId)
                .setContentTitle("正在下载")
                .setContentText(message.name())
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_download)
                .setWhen(System.currentTimeMillis())
                .setStyle(inboxStyle)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(notStr))
//                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(BitmapFactory.decodeResource(getResources(),R.drawable.art_login_bg)))
                .setAutoCancel(true)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        nManager.notify(1,notification);

    }
}
