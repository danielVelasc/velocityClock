package mobile.dp.velocityalarmclock;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
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

public class ClockActivity extends AppCompatActivity implements SetAlarmFragmentListener
{
    SetAlarmFragment setAlarmFragment;
    ListView alarmListView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AlarmCoordinator.getInstance().loadAlarmList(this);
        setContentView(R.layout.activity_clock);
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        myToolbar.setLogo(R.mipmap.velocityclock_templogo);
        setSupportActionBar(myToolbar);

        Log.d("CLOCK_ACTIVITY","onCreate");

        //Register ActivityLifecycleCallbacks in for a lifecycle manager for recording data about
        //the apps current location in the lifecycle.
        getApplication().registerActivityLifecycleCallbacks(new ApplicationLifecycleManager());

        alarmListView = (ListView)findViewById(R.id.alarmListView);
        alarmListView.setAdapter(new AlarmAdapter(this));

        // If the calling intent wants to launch the alarm dialog box do so. ie the alarms going off
        if (getIntent().getBooleanExtra("Launch-Dialog", false)) {
            AlarmRingDialogFragment frag = new AlarmRingDialogFragment();
            frag.show(getSupportFragmentManager(), "AlarmRingDialog");
        }

    }

    /**
     * This function is called by the 'Add' button to create a fragment from which
     * the user can set new alarms
     * @param view the button that calls this function
     */
    void createSetAlarmFragment(View view) {
        Toast.makeText(getApplicationContext(), "The button works", Toast.LENGTH_SHORT).show();

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        myToolbar.setVisibility(View.GONE);

        setAlarmFragment = new SetAlarmFragment();
        fragmentTransaction.add(R.id.set_alarm_container, setAlarmFragment);//
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
                View view = (View)findViewById(R.id.action_add_alarm);
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

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.set_alarm_container));
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragmentTransaction.commit();
        Toolbar myToolbar = (Toolbar)findViewById(R.id.my_toolbar);
        myToolbar.setVisibility(View.VISIBLE);
    }

    /**
     * Saving alarms upon application exit
     */
    @Override
    protected void onStop() {
        super.onStop();

        AlarmCoordinator.getInstance().saveAlarmList(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("CLOCK_ACTIVITY","onDestroy");
    }
}
