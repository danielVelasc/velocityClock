package mobile.dp.velocityalarmclock;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class handles the tasks which should be done when an alarm reaches its time
 * @Author Colin Thompson
 * @Version 1.0
 * @Date February 5th 2017
 */
public class AlarmReceiver extends BroadcastReceiver {
    public final static String TAG = "ALARM_RECEIVER";

    String uuid;
    String name;
    int dayIndex;
    int pendingIntentID;

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
        name = intent.getStringExtra("Alarm-Name");
        dayIndex = intent.getIntExtra("Day-Index", 0);
        pendingIntentID = intent.getIntExtra("Alarm-ID", -1);

        Log.d(TAG, "onReceive received alarm with PendingIntentID = " + pendingIntentID);

        try {
            if (!ApplicationLifecycleManager.isAppVisible()) {
                Log.d(TAG, "App is not visible, loading alarms!");

                AlarmCoordinator.getInstance().loadAlarmList(context);
            }

            Alarm alarm = AlarmCoordinator.getInstance().getAlarmByPendingIntentID(dayIndex, pendingIntentID);
        } catch (NoSuchElementException e) {
            Log.d(TAG, "Caught alarm that no longer exists. Dropping alarm.");

            return;
        }

        Intent dialogIntent = new Intent(context, ClockActivity.class); //Open activity that creates dialog prompt
        dialogIntent.putExtra("Launch-Dialog", true);
        dialogIntent.putExtra("Alarm-ID", pendingIntentID);
        dialogIntent.putExtra("Alarm-Name", name);
        dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        AlarmCoordinator.getInstance().playAlarmNoise(context);

        if (ApplicationLifecycleManager.isAppInForeground()) { //Check if app is open to skip the notification
            context.startActivity(dialogIntent);
        } else {
            createNotification(context, "Alarm", name, "Alarm " + name + "!", dialogIntent);
        }

    }

    /**
     * Creates a notification for a finished alarm.
     * @param context - the context
     * @param msg - The Title text
     * @param msgText The Description Text
     * @param msgAlert - Status bar text
     */
    public void createNotification(Context context, String msg, String msgText, String msgAlert, Intent intent) {

        PendingIntent notIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat.Builder(context) //Create a notification
                .setSmallIcon(R.drawable.bell_icon)
                .setContentTitle(msg)
                .setTicker(msgAlert)
                .setContentText(msgText)
                .setContentIntent(notIntent)
                .setDefaults(NotificationCompat.DEFAULT_SOUND)
                .setAutoCancel(true);

        NotificationManager notManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notManager.notify((new IDGenerator()).getID(), notifyBuilder.build()); //Execute notification
    }
}

/**
 * A singleton class to keep track of notification id's
 */
class IDGenerator {
    //TODO: Should probably be saved somewhere in case app is killed. Could also make the same as alarm id
    private final static AtomicInteger c = new AtomicInteger(0);

    /**
     * @return a unique id.
     */
    public static int getID() {
        return c.incrementAndGet();
    }
}