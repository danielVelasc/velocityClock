package mobile.dp.velocityalarmclock;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * A transparent activity to launch a dialog window from (displaying the alarm going off)
 */
public class RingingAlarm extends AppCompatActivity {

    String nameOfAlarm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent callingIntent = getIntent();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringing_alarm);

        //TODO: Launch Dialog
    }


}
