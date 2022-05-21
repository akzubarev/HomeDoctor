package com.akzubarev.homedoctor.ui.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
                notificationHelper.repeat();
                break;
            case NotificationHelper.DELAY:
                notificationHelper.cancel(id);
                notificationHelper.delay();
                break;
            case NotificationHelper.REMIND:
                Log.d("notifications", "Received REMIND intent");
                notificationHelper.createReminderNotification();
//                notificationHelper.repeat();
                break;
            case NotificationHelper.EXPIRY:
                notificationHelper.createExpiryNotification();
                break;
            case NotificationHelper.SHORTAGE:
                notificationHelper.createShortageNotification();
                break;
        }

    }
}
