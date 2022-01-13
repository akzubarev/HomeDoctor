package com.akzubarev.homedoctor.ui.notifications;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.ui.activities.MainActivity;
import com.akzubarev.homedoctor.data.handlers.DataHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NotificationHelper {

    private final Context context;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    public static final String CLOSE = "close";
    public static final String DELAY = "delay";
    public static final String MAKE = "make";
    public static final String MAKEDELAYED = "makedelayed";
    public static final String OPEN = "open";
    public static final int NOTIFICATION_ID = 0;


    public NotificationHelper(Context context) {
        this.context = context;
    }

    void createNotification() {
        NotificationCompat.Builder mBuilder = configureBuilder();
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            postOreoOptions(mBuilder, nm);

        assert nm != null;
        nm.notify(NOTIFICATION_ID, mBuilder.build());
    }

    private NotificationCompat.Builder configureBuilder() {
        return new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
//                        .setContentTitle("Напоминание")
                .setContentText("Цель на день еще не выполнена")
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(makeIntent(OPEN))
                .addAction(R.drawable.ic_launcher_background, "Отложить на 10 мин.", makeIntent(DELAY))
                .addAction(R.drawable.ic_launcher_background, "Отменить", makeIntent(CLOSE));
    }

    private void postOreoOptions(NotificationCompat.Builder mBuilder, NotificationManager nm) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel =
                    new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                            "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(
                    new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert nm != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            nm.createNotificationChannel(notificationChannel);
        }
    }


    public void setReminder(Calendar calendar, boolean byUser, String intentName) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
//                    AlarmManager.INTERVAL_DAY,
                    makeIntent(intentName));

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM");
            if (byUser)
                Toast.makeText(context,
                        "Напомним " + sdf.format(calendar.getTime()),
                        Toast.LENGTH_SHORT)
                        .show();

        }
    }

    public void setReminder(int hour, int minute, String intentName) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        if (calendar.getTime().compareTo(new Date()) < 0)
            calendar.add(Calendar.DAY_OF_MONTH, 1);

        setReminder(calendar, true, intentName);
    }

    public void repeat() {
        String[] time = DataHandler.getInstance().get("14:00", context).toString().split(":"); //TODO: fix data handling
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[0]);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        setReminder(calendar, false, MAKE);
    }

    public void delay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        setReminder(calendar, true, MAKEDELAYED);
    }

    public void cancelReminder() {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(makeIntent(MAKE));
    }


    public PendingIntent makeIntent(String name) {
        if (name.equals(OPEN)) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setAction(name);
            return PendingIntent.getActivity(context, 0, intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.setAction(name);
            return PendingIntent.getBroadcast(context, 1, intent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        }
    }

    public void cancel(int notificationId) {
        NotificationManager nm =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(notificationId);
    }
}


