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
import com.akzubarev.homedoctor.data.models.Medication;
import com.akzubarev.homedoctor.data.models.Prescription;
import com.akzubarev.homedoctor.data.models.Treatment;
import com.akzubarev.homedoctor.ui.activities.MainActivity;
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.ui.fragments.QRFragment;

import java.util.Calendar;

public class NotificationHelper {

    private final Context context;
    private static final String NOTIFICATION_CHANNEL_ID = "10001";


    public static final String REMIND = "remind";
    public static final int REMINDER_ID = 4;

    public static final String EXPIRY = "expiry";
    public static final int EXPIRY_ID = 1;

    public static final String SHORTAGE = "shortage";
    public static final int SHORTAGE_ID = 2;

    public static final String PRESCRIPTION_END = "prescription";
    private static final int PRESCRIPTION_END_ID = 3;

    public static final String CLOSE = "close";
    public static final String DELAY = "delay";
    public static final String MAKE = "make";
    public static final String MAKEDELAYED = "makedelayed";
    public static final String OPEN = "open";
    public static final String CONFIRM = "confirm";


    DataHandler datahandler;

    public NotificationHelper(Context context) {
        this.context = context;
        datahandler = DataHandler.getInstance(context);
    }

    void createNotification(int id, String message, String additionalInfo, Boolean open) {
        NotificationCompat.Builder mBuilder = configureBuilder(message, id, additionalInfo, open);
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

    private NotificationCompat.Builder configureBuilder(String message, int id, String additionalInfo, Boolean open) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentText(message)
//                        .setContentTitle("Напоминание")
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(false);


        switch (id) {
            case REMINDER_ID:
            default:
                builder.setSubText("Напоминание о приеме")
                        .addAction(R.drawable.ic_alarm_on, "Закрыть", makeIntent(id, CLOSE, ""))
//                        .addAction(R.drawable.ic_alarm_on, "Отложить на 10 мин.", makeIntent(DELAY))
                        .setContentIntent(makeIntent(id, OPEN, ""));
                if (open)
                    builder.addAction(R.drawable.ic_alarm_on, "Подтвердить", makeIntent(id, OPEN, additionalInfo));
                else
                    builder.addAction(R.drawable.ic_alarm_on, "Подтвердить", makeIntent(id, CONFIRM, additionalInfo));
                break;
            case EXPIRY_ID:
                builder.setSubText("Истекает срок годности")
                        .addAction(R.drawable.ic_alarm_on, "Посмотреть", makeIntent(id, OPEN, ""))
                        .addAction(R.drawable.ic_alarm_on, "Закрыть", makeIntent(id, CLOSE, ""));
                break;
            case SHORTAGE_ID:
                builder.setSubText("Заканчиваются лекарства")
                        .addAction(R.drawable.ic_alarm_on, "Посмотреть", makeIntent(id, OPEN, ""))
                        .addAction(R.drawable.ic_alarm_on, "Закрыть", makeIntent(id, CLOSE, ""));
                break;
            case PRESCRIPTION_END_ID:
                builder.setSubText("Закончилось лечение")
                        .addAction(R.drawable.ic_alarm_on, "Закрыть", makeIntent(id, CLOSE, ""));
                break;
        }
        return builder;
    }


    public void setReminder(Calendar calendar, String intentName) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    makeIntent(-1, intentName, ""));
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


    public PendingIntent makeIntent(int id, String name, String additionalInfo) {
        Intent intent;
        switch (name) {
            case OPEN:
                intent = new Intent(context, MainActivity.class);
                intent.putExtra("id", id);
                intent.putExtra("treatmentID", additionalInfo);
                intent.putExtra("destination", "QR");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setAction(name);
                return PendingIntent.getActivity(context, 0, intent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            case CONFIRM:
                intent = new Intent(context, NotificationReceiver.class);
                intent.putExtra("id", id);
                intent.putExtra("treatmentID", additionalInfo);
                intent.setAction(name);
                return PendingIntent.getBroadcast(context, 2 + id, intent,
                        PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
            case CLOSE:
            case DELAY:
            case REMIND:
            case SHORTAGE:
            case EXPIRY:
            default:
                intent = new Intent(context, NotificationReceiver.class);
                intent.putExtra("id", id);
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
        datahandler.getCurrentReminder(treatments -> {
            int count = 0;
            for (Treatment treatment : treatments) {
                int notificationID = REMINDER_ID + count++;
                datahandler.getControlSettings(control ->
                        datahandler.getProfile(treatment.getProfileID(), profile ->
                                datahandler.getMedication(treatment.getMedicationId(), medication ->
                                        datahandler.getPrescription(treatment.getProfileID(),
                                                treatment.getPrescriptionId(),
                                                prescription ->
                                                {
                                                    String message = treatment.getNotification(profile.getName(), prescription.getName(), medication.getName());
                                                    createNotification(notificationID, message, treatment.getDbID(), control);
                                                    Log.d("Creating notification", treatment.getDbID());
                                                    datahandler.getNextReminderTime(calendar -> setReminder(calendar, REMIND));
                                                })
                                )
                        )
                );
            }
        });

    }

    public void createExpiryNotification() {
        datahandler.getExpiryData(message -> createNotification(SHORTAGE_ID, message, "", false));
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
        datahandler.checkEndedPrescriptions(message -> createNotification(PRESCRIPTION_END_ID, message, "", false));
    }

    public void createShortageNotification() {
        datahandler.getShortageData(message -> createNotification(SHORTAGE_ID, message, "", false));
        datahandler.getNextMorningTime(calendar -> setReminder(calendar, EXPIRY));
    }
}