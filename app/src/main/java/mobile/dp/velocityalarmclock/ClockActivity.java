package mobile.dp.velocityalarmclock;



import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


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

        Log.d("CLOCK_ACTIVITY","Start-up");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // get view you want to resize
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.current_time);

        // get layout parameters for that view
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, size.y);

        // initialize new parameters for my element
        mainLayout.setLayoutParams(params);

        Log.d("CLOCK_ACTIVITY", "Height: " + size.y);

        addAlarmButton = (Button)findViewById(R.id.addAlarmButton);

        addAlarmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO Show the view here
            }
        });

    }
}
