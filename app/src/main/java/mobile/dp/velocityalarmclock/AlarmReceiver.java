package mobile.dp.velocityalarmclock;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class handles the tasks which should be done when an alarm reaches its time
 * @Author Colin Thompson
 * @Version 1.0
 * @Date February 5th 2017
 */
public class AlarmReceiver extends BroadcastReceiver {

    String uuid;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String alarmName = intent.getStringExtra("Alarm-Name");
        uuid = intent.getStringExtra("Alarm-ID");
        createNotification(context, "Alarm", alarmName, "Alarm " + alarmName + "!");

    }

    /**
     * Creates a notification for a finished alarm.
     * @param context - the context
     * @param msg - The Title text
     * @param msgText The Description Text
     * @param msgAlert - Status bar text
     */
    public void createNotification(Context context, String msg, String msgText, String msgAlert) {
        Intent intent = new Intent(context, MainActivity.class); //Intent for notification to launch
        intent.putExtra("Alarm-ID", uuid);
        PendingIntent notIntent = PendingIntent.getActivity(context, 0, intent, 0);

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
    //TODO: Should probably be saved somewhere in case app is killed.
    private final static AtomicInteger c = new AtomicInteger(0);

    /**
     * @return a unique id.
     */
    public static int getID() {
        return c.incrementAndGet();
    }
}