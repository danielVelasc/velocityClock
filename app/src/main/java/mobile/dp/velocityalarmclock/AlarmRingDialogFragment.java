package mobile.dp.velocityalarmclock;

import android.app.AlertDialog;
import android.app.Dialog;
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
        dialogBuilder.setMessage("Alarm - " + )
    }



}
