package mobile.dp.velocityalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

/**
 * @author Daniel Velasco
 * @since February 13, 2017
 * @version 1.0
 *
 * This class will manage all alarms set by the user.
 * It is the central point for alarm:
 *      - creation
 *      - deletion
 *      - modification
 *      - aggregation (i.e. alarms will be maintained here)
 *      - serialization/deserialization
 * Classes interested in keeping track of alarm updates should subscribe by calling the
 * register method and implementing the corresponding interface.
 */

class AlarmCoordinator {

    private Context context;
    private ArrayList<Alarm> alarmList;
    private ArrayList<AlarmCoordinatorListener> listeners;

    private static AlarmCoordinator instance = null;

    private AlarmCoordinator () {
        if(instance == null) {
            alarmList = new ArrayList<>();
            listeners = new ArrayList<>();
            instance = new AlarmCoordinator();
        }
    }

    protected static AlarmCoordinator getInstance ()
    { return instance; }

    void createNewAlarm(Alarm alarm) {

        Intent alertIntent = new Intent(context, AlarmReceiver.class); //When timer ends, check with receiver
        alertIntent.putExtra("Alarm-Name", alarm.getName());
        alertIntent.putExtra("Alarm-ID", alarm.getUuid()); // Will be helpful later

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarm.repeats()) //Schedule alarm to repeat if necessary
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTimeToGoOff(), 24 * 60 * 60 * 1000, PendingIntent.getBroadcast(context, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT)); //Repeats every 24 hours after
        else
            alarmMgr.set(AlarmManager.RTC_WAKEUP, alarm.getTimeToGoOff(), PendingIntent.getBroadcast(context, 1, alertIntent, PendingIntent.FLAG_ONE_SHOT));

        alarm.setState(true);
        alarmList.add(alarm);
        notifyAlarmChange();
    }

    void deleteAlarm(Alarm alarm) {





        notifyAlarmChange();
    }

    void modifyAlarm(Alarm alarm) {

        notifyAlarmChange();
    }

    void notifyAlarmChange() {
        for(AlarmCoordinatorListener listener: listeners)
            listener.alarmChanged();
    }

    void registerListener(AlarmCoordinatorListener listener) {
        listeners.add(listener);
    }

    void setContext(Context context) {
        this.context = context;
    }
}
