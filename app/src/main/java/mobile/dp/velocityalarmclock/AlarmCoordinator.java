package mobile.dp.velocityalarmclock;

import android.content.Context;

import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

public class AlarmCoordinator {
    private static final String ALARM_LIST_FILE_NAME = "alarm-list";

    private ArrayList<Alarm> alarmList;
    private ArrayList<AlarmCoordinatorListener> listeners;

    private static final AlarmCoordinator instance = new AlarmCoordinator();

    private AlarmCoordinator () {
        if(instance == null) {
            alarmList = new ArrayList<>();
            listeners = new ArrayList<>();
        }
    }

    public static AlarmCoordinator getInstance ()
    { return instance; }

    void createNewAlarm(Alarm newAlarm) {

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

    /**
     * Saves alarmList to internal storage.
     * @param context Calling context (activity)
     */
    public void saveAlarmList(Context context) {
        ObjectOutputStream outputStream;

        try {
            outputStream =  new ObjectOutputStream(context.openFileOutput(ALARM_LIST_FILE_NAME, Context.MODE_PRIVATE));
            outputStream.writeObject(alarmList);
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
            alarmList = (ArrayList<Alarm>) inputStream.readObject();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

