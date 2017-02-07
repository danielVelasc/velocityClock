package mobile.dp.velocityalarmclock;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * A transparent activity to launch a dialog window from (displaying the alarm going off)
 */
public class RingingAlarm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringing_alarm);

        //Launch dialog
        DialogFragment frag = new AlarmRingDialogFragment();
        frag.show(getSupportFragmentManager(), "missiles");
    }


}
