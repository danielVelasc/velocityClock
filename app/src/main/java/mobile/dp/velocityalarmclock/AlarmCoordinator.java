package mobile.dp.velocityalarmclock;

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

    protected AlarmCoordinator getInstance ()
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

}
