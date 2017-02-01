package mobile.dp.velocityalarmclock;



import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;


/**
 * This class handles the main view in the VelocityAlarmClock application
 *
 * @author Daniel Velasco
 * @since February 1, 2017
 * @version 1
 */
public class ClockActivity extends Activity {

    private Button addAlarmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_activity);

        addAlarmButton = (Button)findViewById(R.id.addAlarmButton);


    }


}
