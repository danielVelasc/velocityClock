package mobile.dp.velocityalarmclock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * This fragment opens a dialog box displaying the alarm that went off and options to snooze and cancel
 * @Author Colin Thompson
 * @Version 1.0
 * @Date February 5th 2017
 */
public class AlarmRingDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ClockActivity ra = (ClockActivity) getActivity();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity()); //Create dialog window
        final AlarmCoordinator ac = AlarmCoordinator.getInstance();

        dialogBuilder.setMessage("Alarm - " + ra.getIntent().getStringExtra("Alarm-Name"))
                .setIcon(R.drawable.bell_icon)
                .setPositiveButton("Snooze", new DialogInterface.OnClickListener() { //Add snooze button
                    public void onClick(DialogInterface dialog, int id) {
                        ac.snoozeAlarm(getContext(), ac.getAlarmByID(ra.getIntent().getStringExtra("Alarm-ID")));
                        dismiss();
                    }
                })
                .setNegativeButton("Stop", new DialogInterface.OnClickListener() { //Add stop button
                   public void onClick(DialogInterface dialog, int id) {

                       dismiss(); // In the future - stop the alarm
                    }
                 });
        return dialogBuilder.create();

    }



}
