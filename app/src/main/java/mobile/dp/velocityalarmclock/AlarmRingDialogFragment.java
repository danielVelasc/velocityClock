package mobile.dp.velocityalarmclock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.NoSuchElementException;

/**
 * This fragment opens a dialog box displaying the alarm that went off and options to snooze and cancel
 * @Author Colin Thompson
 * @Version 1.0
 * @Date February 5th 2017
 */
public class AlarmRingDialogFragment extends DialogFragment {
    Alarm mAlarm;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ClockActivity ra = (ClockActivity) getActivity();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity()); //Create dialog window
        final AlarmCoordinator ac = AlarmCoordinator.getInstance();

        try {
            mAlarm = ac.getAlarmByPendingIntentID(ra.getIntent().getIntExtra("Alarm-ID", 0));

            dialogBuilder.setMessage("Alarm - " + ra.getIntent().getStringExtra("Alarm-Name"))
                    .setIcon(R.drawable.bell_icon)
                    .setPositiveButton("Snooze", new DialogInterface.OnClickListener() { //Add snooze button
                        public void onClick(DialogInterface dialog, int id) {
                            ac.snoozeAlarm(getContext(), mAlarm);
                            dismiss();
                        }
                    })
                    .setNegativeButton("Stop", new DialogInterface.OnClickListener() { //Add stop button
                        public void onClick(DialogInterface dialog, int id) {
                            ac.dismissAlarm(getContext(), mAlarm);
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

    @Override
    public void onStop() {
        try {
            AlarmCoordinator.getInstance().dismissAlarm(getContext(), mAlarm);
        } catch (NullPointerException e) {
            // Alarm was not found, do nothing.
        }
        super.onStop();
    }


}
