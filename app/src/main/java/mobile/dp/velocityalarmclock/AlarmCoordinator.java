package mobile.dp.velocityalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

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

    private static AlarmCoordinator instance = new AlarmCoordinator();

    private AlarmCoordinator () {
        alarmList = new ArrayList<>();
        listeners = new ArrayList<>();

        //TODO: Deserialize the alarms using function
        //getAlarms();

        // Test alarms
        alarmList.add(new Alarm(1, new Date(1000000), true));
        alarmList.add(new Alarm(2, new Date(2000000), false));
        alarmList.add(new Alarm(3, new Date(3000000), true));
        alarmList.add(new Alarm(4, new Date(4000000), true));

        //TODO: Fix issue where one less alarm will be displayed
        // alarmList.add(null);
    }

    protected static AlarmCoordinator getInstance ()
    { return instance; }

    void createNewAlarm(Alarm alarm) {

        Intent alertIntent = new Intent(context, AlarmReceiver.class); //When timer ends, check with receiver
        alertIntent.putExtra("Alarm-Name", alarm.getName());
        alertIntent.putExtra("Alarm-ID", alarm.getUuid()); // Will be helpful later

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarm.repeats()) { //Schedule alarm to repeat if necessary
            PendingIntent scheduledIntent = PendingIntent.getBroadcast(context, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTimeToGoOff(), 24 * 60 * 60 * 1000, scheduledIntent); //Repeats every 24 hours after
            alarm.setIntent(scheduledIntent);
        } else {
            PendingIntent scheduledIntent = PendingIntent.getBroadcast(context, 1, alertIntent, PendingIntent.FLAG_ONE_SHOT);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, alarm.getTimeToGoOff(), PendingIntent.getBroadcast(context, 1, alertIntent, PendingIntent.FLAG_ONE_SHOT));
            alarm.setIntent(scheduledIntent);
        }
        alarm.setState(true);
        alarmList.add(alarm);
        notifyAlarmChange();
    }

    /**
     * First removes the alarm from the alarmList via its ID, then tells the AlarmManager to cancel it via
     * the alarm's scheduledIntent memeber variable. Finally, broadcasts a message to all listeners to do the
     * same.
     * @param alarm the Alarm to be deleted
     */
    void deleteAlarm(Alarm alarm) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // 1. Remove the alarm from AlarmCoordinator's alarmList
        for(int position = 0; position < alarmList.size(); position++){
            if(alarmList.get(position).getUuid() == alarm.getUuid()){
                alarmList.remove(position);
                break;
            }
        }

        // 2. Tell the AlarmManager to cancel the alarm
        alarmMgr.cancel(alarm.getIntent());

        // 3. Broadcast a cancel to all registered Listeners
        for(AlarmCoordinatorListener acl : listeners){
            acl.deleteAlarm(alarm);
        }

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

    ArrayList<Alarm> getAlarmList() {
        return alarmList;
    }

    //TODO Add method to serialize alarms
}
