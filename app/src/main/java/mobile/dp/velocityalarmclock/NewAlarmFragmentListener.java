package mobile.dp.velocityalarmclock;

import android.os.Bundle;

/**
 * @author Daniel Velasco
 * @since February 5, 2017
 *
 * This interface defines communication protocol between a NewAlarmFragment and its
 * parent Activity
 */
public interface NewAlarmFragmentListener {

    /**
     * NewAlarmFragment uses this method to notify Activity that it needs to be removed
     */
    void cancelNewAlarmCreation();

    /**
     * Listeners are notified that the user has submitted information for a new alarm
     * @param newAlarmInfo  Contains all the information for a new alarm to be set
     */
    void submitNewAlarm(Bundle newAlarmInfo);
}
