package mobile.dp.velocityalarmclock;

/**
 * @author Daniel Velasco
 * @since February 06, 2017
 */
public interface SetAlarmFragmentListener {

    /**
     * Call this method to notify that user has cancelled the creation
     * of a new alarm
     */
    public void cancelSetAlarm();

    /**
     * This method is used to notify listeners of a new alarm specified by the user
     * @param newAlarm to be managed
     */
    public void submitNewAlarm(Alarm newAlarm);

}
