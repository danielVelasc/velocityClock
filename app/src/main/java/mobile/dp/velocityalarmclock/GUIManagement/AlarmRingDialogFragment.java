package mobile.dp.velocityalarmclock.GUIManagement;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;
import java.util.NoSuchElementException;

import mobile.dp.velocityalarmclock.Alarm;
import mobile.dp.velocityalarmclock.AlarmManagement.AlarmCoordinator;
import mobile.dp.velocityalarmclock.R;

/**
 * This fragment opens a dialog box displaying the alarm that went off and options to snooze and cancel
 * @Author Colin Thompson
 * @Version 1.0
 * @Date February 5th 2017
 */
public class AlarmRingDialogFragment extends DialogFragment {
    public final static String TAG = "ALARM_RING_DIALOG";

    ClockActivity mClockActivity;
    Alarm mAlarm;
    boolean mAlarmDismissed = false;

    /**
     * Generates a dialog for the alarm that is scheduled to go off; checks the pending
     * intent of the alarm to make sure that the alarm has not been deleted.
     * @param savedInstanceState
     * @return the generated dialog
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mClockActivity = (ClockActivity) getActivity();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity()); //Create dialog window
        final AlarmCoordinator ac = AlarmCoordinator.getInstance();

        try {
            int pendingIntentID = ac.getCurrentPendingIntentID(); // mClockActivity.getIntent().getIntExtra(AlarmCoordinator.ALARM_ID, -1);
            Log.d(TAG, "Looking for alarm with PendingIntentID = " + pendingIntentID);

            mAlarm = ac.getAlarmByPendingIntentID(Calendar.getInstance().getTime().getDay(), pendingIntentID);
            ac.playAlarmNoise(mClockActivity);

            dialogBuilder.setMessage("Alarm - " + mAlarm .getName()) // mClockActivity.getIntent().getStringExtra(AlarmCoordinator.ALARM_NAME))
                    .setIcon(R.drawable.bell_icon)
                    .setPositiveButton("Snooze", new DialogInterface.OnClickListener() { //Add snooze button
                        public void onClick(DialogInterface dialog, int id) {
                            ac.snoozeAlarm(mClockActivity, mAlarm);
                            dismiss();
                        }
                    })
                    .setNegativeButton("Stop", new DialogInterface.OnClickListener() { //Add stop button
                        public void onClick(DialogInterface dialog, int id) {
                            mAlarmDismissed = true;
                            ac.dismissAlarm(mClockActivity, mAlarm);
                            dismiss(); // In the future - stop the alarm
                        }
                    });

            return dialogBuilder.create();
        } catch (NoSuchElementException e) {
            // Alarm was not found, so create an error message that the user can close.

            dialogBuilder.setMessage("Alarm not found").setPositiveButton("Close", new DialogInterface.OnClickListener() { //Add snooze button
                public void onClick(DialogInterface dialog, int id) {
                    dismiss();
                }
            });

            return dialogBuilder.create();
        }
    }

    /**
     * Manages the Alarm Dialog when the application is stopped.
     */
    @Override
    public void onStop() {
        try {
            if (!mAlarmDismissed) {
                AlarmCoordinator.getInstance().dismissAlarm(mClockActivity, mAlarm);
            }
        } catch (NullPointerException e) {
            // Alarm was not found, do nothing.
        }
        super.onStop();
    }


}
