package mobile.dp.velocityalarmclock;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

/**
 * The main activity for the application, managing UI elements and making calls to
 * the rest of the application's code.
 */
public class ClockActivity extends AppCompatActivity implements SetAlarmFragmentListener, AlarmAdapterListener
{
    private static final String TAG = "CLOCK_ACTIVITY";

    private static final String ALARM_DIALOG_FRAGMENT_CREATED = "alarm-dialog-fragment-created";

    SetAlarmFragment setAlarmFragment;
    ListView alarmListView;
    boolean alarmDialogFragmentCreated;

    /**
     * Performs numerous initialization-related tasks
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate - loading alarm data and initializing adapter");

        AlarmCoordinator alarmCoordinator = AlarmCoordinator.getInstance();
        alarmCoordinator.loadAlarmData(this);
        IDGenerator.loadID(this);

        alarmDialogFragmentCreated = false;

        setContentView(R.layout.activity_clock);
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        //myToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(myToolbar);

        //Register ActivityLifecycleCallbacks in for a lifecycle manager for recording data about
        //the apps current location in the lifecycle.
        getApplication().registerActivityLifecycleCallbacks(new ApplicationLifecycleManager());

        AlarmAdapter alarmAdapter = new AlarmAdapter(this, R.layout.single_alarm_element, alarmCoordinator.getAlarmList());
        alarmListView = (ListView)findViewById(R.id.alarmListView);
        alarmListView.setAdapter(alarmAdapter);

        alarmCoordinator.registerListener(alarmAdapter);
    }

    /**
     * onStart method that clears any lingering notifications and checks to see if the alarm launch
     * dialog is waiting to be displayed.
     */
    @Override
    protected void onStart() {
        super.onStart();

        AlarmCoordinator alarmCoordinator = AlarmCoordinator.getInstance();

        // Clear all alarm notifications
        alarmCoordinator.clearAlarmNotifications(this);

        // If the calling intent wants to launch the alarm dialog box do so. ie the alarms going off
        if (!alarmDialogFragmentCreated && alarmCoordinator.getCurrentPendingIntentID() >= 0) {

            /*
            Intent lastAlarmPending = alarmCoordinator.getLastAlarmPending();
            if (lastAlarmPending != null && lastAlarmPending.getIntExtra(AlarmCoordinator.ALARM_ID, -1) == alarmCoordinator.getCurrentPendingIntentID()) {
                // This means that the same alarm dialog intent has gone off already! Do nothing!
                Log.d(TAG, "onStart - NOT creating new alarm launch dialog for id " + alarmCoordinator.getCurrentPendingIntentID());
                return;
            }
            */

            alarmDialogFragmentCreated = true;
            // alarmCoordinator.setLastAlarmPending(getIntent());

            AlarmRingDialogFragment frag = new AlarmRingDialogFragment();
            frag.show(getFragmentManager(), "AlarmRingDialog");
        }
    }

    /**
     * This function is called by the 'Add' button to create a fragment from which
     * the user can set new alarms
     * @param view the button that calls this function
     */
    void createSetAlarmFragment(View view) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        setAlarmFragment = new SetAlarmFragment();
        fragmentTransaction.add(R.id.set_alarm_container, setAlarmFragment);//
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    /**
     * This function is called by tapping an alarm view to create a fragment from which
     * the user can modify an existing alarm
     * @param position the position of the existing alarm to modify
     */
    void createSetAlarmFragment(int position) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        setAlarmFragment = SetAlarmFragment.newInstance(position);
        fragmentTransaction.add(R.id.set_alarm_container, setAlarmFragment);//
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }


    /**
     *  This method is necessary for assigning the appropriate action buttons
     *  to the toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflator = getMenuInflater();
        inflator.inflate(R.menu.actionbar_actions, menu);
        return true;
    }

    /**
     * This method handles the initiation of actions when a user selects
     * one of the action buttons in the taskbar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()) {
            case R.id.action_add_alarm:
                View view = findViewById(R.id.action_add_alarm);
                createSetAlarmFragment(view);
                return true;
            default:
                // handle any actions that for whatever reason do not register
                // as one of the above actions
                Toast.makeText(this, "Error in Taskbar", Toast.LENGTH_SHORT).show();
                return super.onOptionsItemSelected(item);
        }
    }


    /**
     * Closes the NewAlarmFragment
     */
     public void closeSetAlarmFragment() {
         AlarmCoordinator.getInstance().saveAlarmData(this);

         FragmentManager fragmentManager = getFragmentManager();
         FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
         fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.set_alarm_container));
         fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
         fragmentTransaction.commit();
         Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
         myToolbar.setVisibility(View.VISIBLE);
    }

    /**
     * This function handles closing the fragment when the device back button is pressed.
     *
     * Source: http://stackoverflow.com/questions/18755550/fragment-pressing-back-button
     */
    @Override
    public void onBackPressed(){
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }
    }

    /**
     * Saving alarm data and ID master list on activity pause
     */
    @Override
    protected void onPause() {
        Log.d(TAG, "onPause - Saving alarm data");
        AlarmCoordinator.getInstance().saveAlarmData(this);
        IDGenerator.saveID(this);

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(TAG,"onDestroy");
    }

    @Override
    public void alarmViewTapped(int position) {
        createSetAlarmFragment(position);
    }
}
