package com.fu.thinh_nguyen.qrfoodorder.Notification;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.fu.thinh_nguyen.qrfoodorder.R;

public class NotificationHelper {
    private static final String CHANNEL_ID = "default_channel";
    private static final String CHANNEL_NAME = "Thông báo ứng dụng";
    private static final String CHANNEL_DESC = "Kênh thông báo mặc định";

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public static void showNotification(Context context, String title, String message) {
        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 1. Tạo channel (Android O trở lên) với âm thanh mặc định
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH    // cao để có âm thanh + hiển thị nổi bật
            );
            channel.setDescription(CHANNEL_DESC);

            // Sử dụng âm thanh mặc định
            Uri defaultSoundUri = Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_sound);
            // Hoặc dùng âm thanh system mặc định:
//          channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, new AudioAttributes.Builder()
//                  .setUsage(AudioAttributes.USAGE_NOTIFICATION)
//                  .build());

            // Nếu bạn có file raw/notification_sound.mp3 trong res/raw, dùng defaultSoundUri
            channel.setSound(defaultSoundUri, new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());

            manager.createNotificationChannel(channel);
        }

        // 2. Xây dựng notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)  // icon
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                // bật âm thanh và vibration mặc định
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                // nếu muốn dùng custom sound:
                // .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.notification_sound))
                ;

        // 3. Show notification
        NotificationManagerCompat.from(context)
                .notify((int) System.currentTimeMillis(), builder.build());
    }
}
