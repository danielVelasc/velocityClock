package mobile.dp.velocityalarmclock;



import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.app.Fragment;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class handles the main view in the VelocityAlarmClock application
 *
 * @author Daniel Velasco
 * @since February 1, 2017
 * @version 1
 */
public class ClockActivity extends Activity implements TestFragment.OnTestFragmentInteractionListener {

    FloatingActionButton addAlarmButton;
    Fragment addAlarmFragment; // todo make sure that the custom class is referenced here instead'
    TextClock digitalClock;
    Alarm[] userAlarms;

    final String fileName = "ALARM_SAVE_FILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clock_activity);

        Log.d("CLOCK_ACTIVITY","onCreate");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Get view to resize and layout parameters for that view
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.ClockLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, size.y);

        // Initialize new parameters for my element
        mainLayout.setLayoutParams(params);

        Log.d("CLOCK_ACTIVITY", "Height: " + size.y);

        digitalClock = (TextClock)findViewById(R.id.digitalClock);

        addAlarmButton = (FloatingActionButton) findViewById(R.id.addAlarmButton);

        // Retrieve user alarms from file system

    }

    /**
     * This method populates the array of alarms that have been set by a user
     */
    private void getAlarms() {

        Log.d("CLOCK_ACTIVITY","getAlarms");

        try {
            FileInputStream fis = this.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            Alarm simpleClass = (Alarm) is.readObject();
            is.close();
            fis.close();
        } catch(ClassNotFoundException | IOException a) {
            a.printStackTrace();
            System.err.println("Error getting saved alarms");
        }
    }

    /**
     * Called when FAB is clicked. Must be public for XML file to locate.
     * @param view The FloatingActionButton that was clicked
     */
    public void createNewAlarm(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        TestFragment fragment = new TestFragment();
        fragmentTransaction.add(R.id.new_alarm_container, fragment);
        fragmentTransaction.commit();

        View fab = findViewById(R.id.addAlarmButton);
        fab.setVisibility(View.GONE);

        // Disable user input on activity
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onCloseNewAlarmView(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.remove(fragment);
        fragmentTransaction.commit();

        View fab = findViewById(R.id.addAlarmButton);
        fab.setVisibility(View.VISIBLE);

        // Re-enable user input on activity
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
    /**
     * Saving alarms upon application exit
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        try{
            getFilesDir();
            FileOutputStream fos = this.openFileOutput(fileName, this.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(this);
            os.close();
            fos.close();
        } catch(IOException e) {
            e.printStackTrace();
            System.err.println("Error saving alarms");
        }

    }
}
