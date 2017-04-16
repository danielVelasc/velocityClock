package mobile.dp.velocityalarmclock;

/**
 * Simple Interface to be implemented by classes that need to be notified when
 * the user interacts with the Alarm view GUI component.
 * Created by ferna on 2017-03-06.
 */
public interface AlarmAdapterListener {
    void alarmViewTapped(int position);
}
