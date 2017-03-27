package mobile.dp.velocityalarmclock;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
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
        name = intent.getStringExtra("Alarm-Name");
        dayIndex = intent.getIntExtra("Day-Index", 0);
        pendingIntentID = intent.getIntExtra("Alarm-ID", -1);
        pendingIntent = intent.getParcelableExtra("Pending-Intent");

        Log.d(TAG, "onReceive received alarm with PendingIntentID = " + pendingIntentID);
        AlarmCoordinator alarmCoordinator = AlarmCoordinator.getInstance();

        try {
            if (!ApplicationLifecycleManager.isAppVisible()) {
                Log.d(TAG, "App is not visible, loading alarms!");
                alarmCoordinator.loadAlarmList(context);
            }

            // This will throw a NoSuchElementException if the alarm no longer exists, or was modified
            alarmCoordinator.getAlarmByPendingIntentID(dayIndex, pendingIntentID);

            // Open activity that creates dialog prompt
            Intent dialogIntent = new Intent(context, ClockActivity.class);
            dialogIntent.putExtra("Launch-Dialog", true);
            dialogIntent.putExtra("Alarm-ID", pendingIntentID);
            dialogIntent.putExtra("Alarm-Name", name);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            alarmCoordinator.playAlarmNoise(context);

            // Add alarm to pending alarm list
            alarmCoordinator.addPendingAlarm(context, dialogIntent);

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

/**
 * A singleton class to keep track of notification id's
 */
class IDGenerator implements Serializable {
    private static final long serialVersionUID = 183742938754926378L;
    private static final String ID_GENERATOR_FILE_NAME = "id-generator";

    //TODO: Should probably be saved somewhere in case app is killed. Could also make the same as alarm id
    private final static AtomicInteger c = new AtomicInteger(0);

    /**
     * @return a unique id.
     */
    public static int getID() {
        return c.incrementAndGet();
    }

    public static void saveID(Context context) {
        ObjectOutputStream outputStream;

        try {
            outputStream =  new ObjectOutputStream(context.openFileOutput(ID_GENERATOR_FILE_NAME, Context.MODE_PRIVATE));

            outputStream.writeInt(c.intValue());

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadID(Context context) {
        ObjectInputStream inputStream;

        try {
            inputStream =  new ObjectInputStream(context.openFileInput(ID_GENERATOR_FILE_NAME));

            c.set(inputStream.readInt());

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}