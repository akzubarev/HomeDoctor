package com.akzubarev.homedoctor.ui.notifications;

import static android.content.Context.ALARM_SERVICE;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.akzubarev.homedoctor.R;
import com.akzubarev.homedoctor.data.models.Treatment;
import com.akzubarev.homedoctor.ui.activities.MainActivity;
import com.akzubarev.homedoctor.data.handlers.DataHandler;

import java.util.Calendar;

public class NotificationHelper {

    private final Context context;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";

    public static final String REMIND = "remind";
    public static final int REMINDER_ID = 0;

    public static final String EXPIRY = "expiry";
    public static final int EXPIRY_ID = 1;

    public static final String SHORTAGE = "shortage";
    public static final int SHORTAGE_ID = 2;

    public static final String CLOSE = "close";
    public static final String DELAY = "delay";
    public static final String MAKE = "make";
    public static final String MAKEDELAYED = "makedelayed";
    public static final String OPEN = "open";
    private static final String CONFIRM = "confirm";


    DataHandler datahandler;

    public NotificationHelper(Context context) {
        this.context = context;
        datahandler = DataHandler.getInstance(context);
    }

    void createNotification(int id, String message) {
        NotificationCompat.Builder mBuilder = configureBuilder(message, id);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                "NOTIFICATION_CHANNEL_NAME", importance);
        notificationChannel.enableLights(true);
        notificationChannel.setLightColor(Color.RED);
        notificationChannel.enableVibration(true);
        notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
        nm.createNotificationChannel(notificationChannel);

        nm.notify(id, mBuilder.build());

        Log.d("notifications", "Notified");
    }

    private NotificationCompat.Builder configureBuilder(String message, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
//                        .setContentTitle("Напоминание")
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message));


        switch (id) {
            case REMINDER_ID:
                builder.addAction(R.drawable.ic_launcher_background, "Подтвердить", makeIntent(CONFIRM))
                        .addAction(R.drawable.ic_launcher_background, "Отложить на 10 мин.", makeIntent(DELAY))
                        .setContentIntent(makeIntent(OPEN))
                        .setAutoCancel(false);
                break;
            case EXPIRY_ID:
            case SHORTAGE_ID:
                builder.addAction(R.drawable.ic_launcher_background, "Посмотреть", makeIntent(OPEN))
                        .addAction(R.drawable.ic_launcher_background, "Закрыть", makeIntent(CLOSE));
                break;
        }
        return builder;
    }


    public void setReminder(Calendar calendar, String intentName) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), makeIntent(intentName));
            Log.d("notifications", "ExactSet");
        }
    }

    public void repeat() {
        String[] time = DataHandler.getInstance(context).get("14:00", context).toString().split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[0]);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        setReminder(calendar, MAKE);
    }

    public void delay() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 10);
        setReminder(calendar, MAKEDELAYED);
    }


    public PendingIntent makeIntent(String name) {
        Intent intent;
        switch (name) {
            case OPEN:
                intent = new Intent(context, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setAction(name);
                return PendingIntent.getActivity(context, 0, intent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            default:
                intent = new Intent(context, NotificationReceiver.class);
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

    public void createReminderNotification() {
        datahandler.getCurrentReminder(treatment -> {
            String message = treatment.getNotification();
            createNotification(REMINDER_ID, message);
            datahandler.getNextReminderTime(calendar -> setReminder(calendar, REMIND));
            Log.d("notifications", "got current reminder " + treatment.getNotification());
        });

    }

    public void createExpiryNotification() {
        datahandler.getExpiryData(message -> createNotification(SHORTAGE_ID, message));
        datahandler.getNextMorningTime(calendar -> setReminder(calendar, EXPIRY));
    }

    public void setUpNotification(String intent) {
        switch (intent) {
            case EXPIRY:
                datahandler.getExpiryData((message) -> {
                    if (message != null)
                        createExpiryNotification();
                    else {
                        datahandler.getNextMorningTime(calendar -> setReminder(calendar, EXPIRY));
                    }
                });
                break;
            case SHORTAGE:
                datahandler.getShortageData((message) -> {
                    if (message != null)
                        createShortageNotification();
                    else {
                        datahandler.getNextMorningTime(calendar -> setReminder(calendar, SHORTAGE));
                    }
                });
                break;
            case REMIND:
                datahandler.getNextReminderTime(calendar -> setReminder(calendar, REMIND));
                break;
        }
    }

    public void createShortageNotification() {
        datahandler.getShortageData(message -> createNotification(SHORTAGE_ID, message));
        datahandler.getNextMorningTime(calendar -> setReminder(calendar, EXPIRY));

    }
}


