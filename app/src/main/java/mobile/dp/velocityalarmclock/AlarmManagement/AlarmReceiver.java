package mobile.dp.velocityalarmclock.AlarmManagement;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.NoSuchElementException;

import mobile.dp.velocityalarmclock.AlarmManagement.AlarmCoordinator;
import mobile.dp.velocityalarmclock.AlarmManagement.ApplicationLifecycleManager;
import mobile.dp.velocityalarmclock.GUIManagement.ClockActivity;
import mobile.dp.velocityalarmclock.IDGenerator;
import mobile.dp.velocityalarmclock.R;

/**
 * This class handles the tasks which should be done when an alarm reaches its time
 * @Author Colin Thompson
 * @Version 1.0
 * @Date February 5th 2017
 */
public class AlarmReceiver extends BroadcastReceiver {
    public final static String TAG = "ALARM_RECEIVER";

    String name;
    int dayIndex;
    int pendingIntentID;
    PendingIntent pendingIntent;

    public AlarmReceiver() {
    }

    /**
     * Called if alarm time has been reached. Determines if the app is in use, if so brings to main
     * activity and displays dialog. Otherwise, create a notification to do the same. This will pass
     * along intent extras provided by the AlarmManager.
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        name = intent.getStringExtra(AlarmCoordinator.ALARM_NAME);
        pendingIntentID = intent.getIntExtra(AlarmCoordinator.ALARM_ID, -1);
        dayIndex = intent.getIntExtra(AlarmCoordinator.ALARM_DAY_INDEX, 0);
        pendingIntent = intent.getParcelableExtra(AlarmCoordinator.ALARM_PENDING_INTENT);

        Log.d(TAG, "onReceive received alarm with PendingIntentID = " + pendingIntentID);
        AlarmCoordinator alarmCoordinator = AlarmCoordinator.getInstance();

        try {
            if (!ApplicationLifecycleManager.isAppVisible()) {
                Log.d(TAG, "App is not visible, loading alarms!");
                alarmCoordinator.loadAlarmData(context);
            }

            // This will throw a NoSuchElementException if the alarm no longer exists, or was modified
            alarmCoordinator.getAlarmByPendingIntentID(dayIndex, pendingIntentID);

            // Open activity that creates dialog prompt
            Intent dialogIntent = new Intent(context, ClockActivity.class);
            dialogIntent.putExtra(AlarmCoordinator.ALARM_LAUNCH_DIALOG, true);
            dialogIntent.putExtra(AlarmCoordinator.ALARM_ID, pendingIntentID);
            dialogIntent.putExtra(AlarmCoordinator.ALARM_NAME, name);
            dialogIntent.putExtra(AlarmCoordinator.ALARM_DAY_INDEX, dayIndex);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            alarmCoordinator.playAlarmNoise(context);

            // Add alarm to pending alarm list
            alarmCoordinator.addPendingAlarm(dialogIntent);

            // Save new pending alarm
            alarmCoordinator.saveAlarmData(context);

            // Check if app is open to skip the notification
            if (ApplicationLifecycleManager.isAppVisible()) {
                alarmCoordinator.startPendingAlarm(context);
            } else {
                alarmCoordinator.addAlarmNotification(createNotification(context, "Alarm", name, "Alarm " + name + "!", dialogIntent));
            }

        } catch (NoSuchElementException e) {
            // Here we take the pendingIntent and cancel it so that repeating alarms don't clog the system
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
            Log.d(TAG, "Caught alarm that no longer exists. Dropping and cancelling alarm.");
        }
    }

    /**
     * Creates a notification for a finished alarm.
     * @param context - the context
     * @param msg - The Title text
     * @param msgText The Description Text
     * @param msgAlert - Status bar text
     * @return the notification ID
     */
    public int createNotification(Context context, String msg, String msgText, String msgAlert, Intent intent) {

        PendingIntent notIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context) //Create a notification
                .setSmallIcon(R.drawable.bell_icon)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText)
                .setContentIntent(notIntent)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setAutoCancel(true);

        int id = IDGenerator.getID();
        NotificationManager notManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notManager.notify(id, notifyBuilder.build()); //Execute notification

        return id;
    }
}

