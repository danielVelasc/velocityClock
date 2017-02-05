package mobile.dp.velocityalarmclock;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ClockActivity extends AppCompatActivity implements NewAlarmFragmentListener {

    NewAlarmFragment createNewAlarmFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        String date = new SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        TextView weekday_month_day = (TextView) findViewById(R.id.dateTextView);
        weekday_month_day.setText(date);

        Log.d("CLOCK_ACTIVITY","onCreate");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Get view to resize and layout parameters for that view
        RelativeLayout clockLayout = (RelativeLayout) findViewById(R.id.clockRelativeLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, size.y);
        clockLayout.setLayoutParams(params);

    }

    void createAddNewAlarmFragment(View view) {
        Toast.makeText(getApplicationContext(), "The button works", Toast.LENGTH_SHORT).show();

// TODO Uncomment once the fragment has been added to the project
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//        createNewAlarmFragment = new NewAlarmFragment();
//        fragmentTransaction.add(R.id.fragment_container, createNewAlarmFragment);
//        fragmentTransaction.commit();
    }

    public void cancelNewAlarmCreation() {
        closeNewAlarmFragment();
    }

    public void submitNewAlarm(Bundle newAlarmInfo) {
        // TODO create a new alarm

        closeNewAlarmFragment();
    }


    private void closeNewAlarmFragment() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(createNewAlarmFragment);
        fragmentTransaction.commit();

    }

}
