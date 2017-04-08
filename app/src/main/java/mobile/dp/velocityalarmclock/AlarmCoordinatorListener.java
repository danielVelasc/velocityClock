package mobile.dp.velocityalarmclock;

/**
 * @author Daniel Velasco
 * @since February 13, 2017
 *
 * This interface is implemented by classes that need to receive notifications
 * every time an alarm changes (i.e. deletions, active status, modifications)
 */

public interface AlarmCoordinatorListener {
    void alarmChanged();
}
