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
import com.akzubarev.homedoctor.data.handlers.DataHandler;
import com.akzubarev.homedoctor.data.models.Treatment;
import com.akzubarev.homedoctor.ui.activities.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

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
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(false);


        switch (id) {
            case REMINDER_ID:
            default:
                builder.setSubText("?????????????????????? ?? ????????????")
                        .addAction(R.drawable.ic_alarm_on, "??????????????", makeIntent(id, CLOSE, ""))
//                        .addAction(R.drawable.ic_alarm_on, "???????????????? ???? 10 ??????.", makeIntent(DELAY))
                        .setContentIntent(makeIntent(id, OPEN, ""));
                if (open)
                    builder.addAction(R.drawable.ic_alarm_on, "??????????????????????", makeIntent(id, OPEN, additionalInfo));
                else
                    builder.addAction(R.drawable.ic_alarm_on, "??????????????????????", makeIntent(id, CONFIRM, additionalInfo));
                break;
            case EXPIRY_ID:
                builder.setSubText("???????????????? ???????? ????????????????")
                        .addAction(R.drawable.ic_alarm_on, "????????????????????", makeIntent(id, OPEN, ""))
                        .addAction(R.drawable.ic_alarm_on, "??????????????", makeIntent(id, CLOSE, ""));
                break;
            case SHORTAGE_ID:
                builder.setSubText("?????????????????????????? ??????????????????")
                        .addAction(R.drawable.ic_alarm_on, "????????????????????", makeIntent(id, OPEN, ""))
                        .addAction(R.drawable.ic_alarm_on, "??????????????", makeIntent(id, CLOSE, ""));
                break;
            case PRESCRIPTION_END_ID:
                builder.setSubText("?????????????????????? ??????????????")
                        .addAction(R.drawable.ic_alarm_on, "??????????????", makeIntent(id, CLOSE, ""));
                break;
        }
        return builder;
    }


    public void setReminder(Calendar calendar, String intentName, int id) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    makeIntent(id, intentName, ""));
        }
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
            if (treatments.size() == 0)
                datahandler.getNextReminderTime(calendar -> setReminder(calendar, REMIND, REMINDER_ID));
            else
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
                                                        datahandler.getNextReminderTime(calendar -> setReminder(calendar, REMIND, REMINDER_ID));
                                                    })
                                    )
                            )
                    );
                }
        });

    }

    public void updateMorning() {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM HH:mm",
                new Locale("ru", "RU"));
        datahandler.getNextMorningTime(calendar -> {
            setReminder(calendar, EXPIRY, -EXPIRY_ID);
            setReminder(calendar, SHORTAGE, -SHORTAGE_ID);
            Log.d("MORNING", format.format(calendar.getTime()));
        });


    }

    public void createExpiryNotification() {
        datahandler.getExpiryData(message -> createNotification(EXPIRY_ID, message, "", false));
        datahandler.getNextMorningTime(calendar -> setReminder(calendar, EXPIRY, -EXPIRY_ID));
    }

    public void createShortageNotification() {
        datahandler.getShortageData(message -> createNotification(SHORTAGE_ID, message, "", false));
        datahandler.getNextMorningTime(calendar -> setReminder(calendar, EXPIRY, -EXPIRY_ID));
    }

    public void setUpNotification(String intent) {
        switch (intent) {
            case EXPIRY:
                datahandler.getExpiryData((message) -> {
                    Log.d("EXPIRY", message);
                    if (message != null)
                        createExpiryNotification();
                    else {
                        datahandler.getNextMorningTime(calendar -> setReminder(calendar, EXPIRY, -EXPIRY_ID));
                    }
                });
                break;
            case SHORTAGE:
                datahandler.getShortageData((message) -> {
                    if (message != null)
                        createShortageNotification();
                    else {
                        datahandler.getNextMorningTime(calendar -> setReminder(calendar, SHORTAGE, -SHORTAGE_ID));
                    }
                });
                break;
            case REMIND:
                datahandler.getNextReminderTime(calendar -> setReminder(calendar, REMIND, REMINDER_ID));
                break;
        }
        datahandler.checkEndedPrescriptions(message -> createNotification(PRESCRIPTION_END_ID, message, "", false));
    }

}