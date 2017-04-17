package mobile.dp.velocityalarmclock.AlarmManagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import mobile.dp.velocityalarmclock.AlarmManagement.AlarmCoordinator;

/**
 * @author Aidan Bailey
 * @since March 13, 2017
 *
 * This broadcast receiver is for the sole purpose of rescheduling all of the alarms upon boot.
 * Upon boot, a Boot_Completed message is broadcast; this receiver detects that broadcast and
 * calls the reschedule (to-do) method of Coordinator. (See the Android Manifest for more.)
 */

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent){
        AlarmCoordinator.getInstance().rescheduleAlarms(context);
    }
}
