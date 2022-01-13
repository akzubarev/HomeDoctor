package com.akzubarev.homedoctor.ui.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);

        String action = intent.getAction();

        switch (action) {
            case NotificationHelper.CLOSE:
                notificationHelper.cancel(NotificationHelper.NOTIFICATION_ID);
                notificationHelper.repeat();
                break;
            case NotificationHelper.DELAY:
                notificationHelper.cancel(NotificationHelper.NOTIFICATION_ID);
                notificationHelper.delay();
                break;
            case NotificationHelper.MAKE:
//                if (!Utils.reachedGoal(context))
//                    notificationHelper.createNotification();
                notificationHelper.repeat();
                break;
            case NotificationHelper.MAKEDELAYED:
//                if (!Utils.reachedGoal(context))
//                    notificationHelper.createNotification();
                break;
        }

    }
}
