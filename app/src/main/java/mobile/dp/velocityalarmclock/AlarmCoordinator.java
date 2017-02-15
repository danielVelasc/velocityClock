package mobile.dp.velocityalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;

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
    private static final String ALARM_LIST_FILE_NAME = "alarm-list";

    private ArrayList<Alarm> alarmList;
    private ArrayList<AlarmCoordinatorListener> listeners;

    private static final AlarmCoordinator instance = new AlarmCoordinator();

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

    void createNewAlarm(Context context, Alarm alarm) {

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

        // delete stuff from array list
        // delete stuff from system

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

    ArrayList<Alarm> getAlarmList() {
        return alarmList;
    }

    //TODO Add method to serialize alarms
    /**
     * Saves alarmList to file in internal storage.
     * @param context Calling context (activity)
     */
    public void saveAlarmList(Context context) {
        ObjectOutputStream outputStream;

        try {
            outputStream =  new ObjectOutputStream(context.openFileOutput(ALARM_LIST_FILE_NAME, Context.MODE_PRIVATE));

            if (alarmList == null) {
                outputStream.writeObject(new ArrayList<Alarm>());
            } else {
                outputStream.writeObject(alarmList);
            }

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads file containing alarms in internal storage into alarmList.
     * @param context Calling context (activity)
     */
    public void loadAlarmList(Context context) {
        ObjectInputStream inputStream;

        try {
            inputStream =  new ObjectInputStream(context.openFileInput(ALARM_LIST_FILE_NAME));
            ArrayList<Alarm> list = (ArrayList<Alarm>) inputStream.readObject();

            if (list != null) {
                alarmList = list;
            }

            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

