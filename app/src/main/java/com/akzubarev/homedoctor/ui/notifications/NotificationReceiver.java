package com.akzubarev.homedoctor.ui.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.akzubarev.homedoctor.data.handlers.DataHandler;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);

        Log.d("notifications", "Received some intent");
        String action = intent.getAction();
        int id = intent.getIntExtra("id", -1);
        switch (action) {
            case NotificationHelper.CLOSE:
                notificationHelper.cancel(id);
                break;
            case NotificationHelper.REMIND:
                notificationHelper.createReminderNotification();
                break;
            case NotificationHelper.EXPIRY:
                notificationHelper.createExpiryNotification();
                break;
            case NotificationHelper.SHORTAGE:
                notificationHelper.createShortageNotification();
                break;
            case NotificationHelper.CONFIRM:
                DataHandler dataHandler = DataHandler.getInstance(context);
                String treatmentID = intent.getStringExtra("treatmentID");
                Log.d("Received treatmentID", treatmentID);
                notificationHelper.cancel(id);
                if (!treatmentID.isEmpty())
                    dataHandler.getTreatment(treatmentID, treatment ->
                            dataHandler.getProfile(treatment.getProfileID(), profile ->
                                    dataHandler.getPrescription(treatment.getProfileID(), treatment.getPrescriptionId(), prescription ->
                                            dataHandler.getMedication(treatment.getMedicationId(), medication -> {
                                                        medication.take();
                                                        dataHandler.saveMedication(medication, () -> {
                                                            notificationHelper.setUpNotification(NotificationHelper.EXPIRY);
                                                            notificationHelper.setUpNotification(NotificationHelper.SHORTAGE);
                                                        });

                                                        Calendar calendar = Calendar.getInstance();
                                                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", new Locale("ru"));
                                                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", new Locale("ru"));
                                                        treatment.setDay(dateFormat.format(calendar.getTime()));
                                                        treatment.setTime(timeFormat.format(calendar.getTime()));
                                                        treatment.setPrescriptionId(prescription.getName());
                                                        treatment.setProfileID(profile.getName());
                                                        dataHandler.saveOldTreatment(treatment);
                                                    }
                                            )
                                    )
                            )
                    );
                break;
        }
    }
}
