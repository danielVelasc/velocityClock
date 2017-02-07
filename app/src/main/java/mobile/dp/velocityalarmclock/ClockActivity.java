package mobile.dp.velocityalarmclock;

<<<<<<< HEAD


import android.app.Activity;
=======
>>>>>>> fv_branch
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Point;
import android.os.Bundle;
<<<<<<< HEAD
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.app.Fragment;
import android.view.WindowManager;
=======
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
>>>>>>> fv_branch
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

<<<<<<< HEAD
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
=======
public class ClockActivity extends AppCompatActivity implements NewAlarmFragmentListener {

    NewAlarmFragment createNewAlarmFragment;
    ArrayList<Alarm> alarmList;

>>>>>>> fv_branch

    final String fileName = "ALARM_SAVE_FILE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        Log.d("CLOCK_ACTIVITY","onCreate");

        //Register ActivityLifecycleCallbacks in for a lifecycle manager for recording data about
        //the apps current location in the lifecycle.
        getApplication().registerActivityLifecycleCallbacks(new ApplicationLifecycleManager());

        String date = new SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH).format(Calendar.getInstance().getTime());
        TextView weekday_month_day = (TextView) findViewById(R.id.dateTextView);
        weekday_month_day.setText(date);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        // Get view to resize and layout parameters for that view
        RelativeLayout clockLayout = (RelativeLayout) findViewById(R.id.clockRelativeLayout);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, size.y);
        clockLayout.setLayoutParams(params);

        // TODO: Deserialize the alarms
        //getAlarms();
        ArrayList<Alarm> alarms = new ArrayList<Alarm>();
        alarms.add(new Alarm(1, new Date(1000000), true));
        alarms.add(new Alarm(2, new Date(2000000), false));
        alarms.add(new Alarm(3, new Date(3000000), true));


        AlarmAdapter alarmAdapter = new AlarmAdapter(this, alarms);
        ListView alarmListView = (ListView)findViewById(R.id.alarmListView);
        alarmListView.setAdapter(alarmAdapter);

    }

    /**
     * This function is called by the 'Add' button to create a fragment from which
     * the user can set new alarms
     * @param view the button that calls this function
     */
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


    /**
     * Closes the NewAlarmFragment
     */
    private void closeNewAlarmFragment() {

<<<<<<< HEAD
        addAlarmButton = (FloatingActionButton) findViewById(R.id.addAlarmButton);
=======
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
>>>>>>> fv_branch

        fragmentTransaction.remove(createNewAlarmFragment);
        fragmentTransaction.commit();

    }

    /**
     * This method populates the array of alarms that have been set by a user
     */
    private void getAlarms() {

        Log.d("CLOCK_ACTIVITY","getAlarms");

        try {
<<<<<<< HEAD
=======
            String fileName = getFilesDir() + "/alarms";
>>>>>>> fv_branch
            FileInputStream fis = this.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            alarmList = (ArrayList<Alarm>) is.readObject();
            is.close();
            fis.close();
        } catch(ClassNotFoundException a) {
            System.out.println("No alarms have been saved ");
        }
        catch(IOException a)
        {
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

<<<<<<< HEAD
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
=======
>>>>>>> fv_branch
    /**
     * Saving alarms upon application exit
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("CLOCK_ACTIVITY","getAlarms");
        try{
            String fileName = getFilesDir() + "/alarms";
            FileOutputStream fos = this.openFileOutput(fileName, this.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(alarmList);
            os.close();
            fos.close();
        } catch(IOException e) {
            e.printStackTrace();
            System.err.println("Error saving alarms");
        }

    }
}
