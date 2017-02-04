package mobile.dp.velocityalarmclock;



import android.app.Activity;
import android.os.Bundle;
import android.view.View;
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
    //private ClockView;
    //private NewAlarmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_activity);

        addAlarmButton = (Button)findViewById(R.id.addAlarmButton);

        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Show the view here
            }
        });

    }


}
