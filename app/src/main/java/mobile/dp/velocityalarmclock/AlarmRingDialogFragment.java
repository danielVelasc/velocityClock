package mobile.dp.velocityalarmclock;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Created by colinthompson on 2017-02-06.
 */

public class AlarmRingDialogFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        RingingAlarm ra = (RingingAlarm) getActivity();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setMessage("Alarm - " + ra.getIntent().getStringExtra("Alarm-Name"))
                .setPositiveButton("Snooze", new DialogInterface.OnClickListener() { //Add snooze button
                    public void onClick(DialogInterface dialog, int id) {
                        //Snooze the alarm
                    }
                })
                .setNegativeButton("Stop", new DialogInterface.OnClickListener() { //Add stop button
                   public void onClick(DialogInterface dialog, int id) {
                        //Stop the alarm
                    }
                 });
        return dialogBuilder.create();

    }



}
