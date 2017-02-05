package mobile.dp.velocityalarmclock;



import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;


/**
 * This class handles the main view in the VelocityAlarmClock application
 *
 * @author Daniel Velasco
 * @since February 1, 2017
 * @version 1
 */
public class ClockActivity extends Activity {

    private Button addAlarmButton;
    private Button backButton;
    //private ClockView;
    //private NewAlarmView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_activity);
        final FrameLayout frameLayout = (FrameLayout) findViewById(R.id.frame);
        addAlarmButton = (Button) findViewById(R.id.addAlarmButton);
        backButton = (Button) findViewById(R.id.backButton);
        frameLayout.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);

        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment setAlarm = new SetAlarmFragment();
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                //ft.addToBackStack("tag");
                ft.replace(R.id.frame, setAlarm);
                ft.commit();
                frameLayout.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.VISIBLE);
                backButton.bringToFront();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                frameLayout.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
            }
        });



    }

}
