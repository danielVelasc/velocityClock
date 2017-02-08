package mobile.dp.velocityalarmclock;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class ClockActivity extends AppCompatActivity implements SetAlarmFragmentListener
{

    //NewAlarmFragment createNewAlarmFragment;
    ArrayList<Alarm> alarmList = new ArrayList<Alarm>();
    SetAlarmFragment setAlarmFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        Log.d("CLOCK_ACTIVITY","onCreate");

        //Register ActivityLifecycleCallbacks in for a lifecycle manager for recording data about
        //the apps current location in the lifecycle.
        getApplication().registerActivityLifecycleCallbacks(new ApplicationLifecycleManager());

        //TODO: Deserialize the alarms using function
        //getAlarms();

        // Test alarms
        alarmList.add(new Alarm(1, new Date(1000000), true));
        alarmList.add(new Alarm(2, new Date(2000000), false));
        alarmList.add(new Alarm(3, new Date(3000000), true));
        alarmList.add(new Alarm(4, new Date(4000000), true));

        //TODO: Fix issue where one less alarm will be displayed
        // alarmList.add(null);


        AlarmAdapter alarmAdapter = new AlarmAdapter(this, alarmList);
        ListView alarmListView = (ListView)findViewById(R.id.alarmListView);
        alarmListView.setAdapter(alarmAdapter);

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

        setAlarmFragment = new SetAlarmFragment();
        fragmentTransaction.add(R.id.set_alarm_container, setAlarmFragment);//
        fragmentTransaction.commit();
    }

    public void cancelSetAlarm() {
        closeNewAlarmFragment();
    }


    /**
     * Enable an alarm.
     * @param alarm
     */
    public void submitNewAlarm(Alarm alarm) {

        Intent alertIntent = new Intent(this, AlarmReceiver.class); //When timer ends, check with reciever
        alertIntent.putExtra("Alarm-Name", alarm.getName());
        alertIntent.putExtra("Alarm-ID", alarm.getUuid()); //Will probably be helpful later

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        //TODO: Make Repeating alarms work for variable time between repeats
        if (alarm.repeats()) //Schedule alarm to repeat if necessary
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTimeToGoOff(), 24 * 60 * 60 * 1000, PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT)); //Repeats every 24 hours after
        else
            alarmMgr.set(AlarmManager.RTC_WAKEUP, alarm.getTimeToGoOff(), PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_ONE_SHOT));

        alarm.setState(true);

        closeNewAlarmFragment();
    }


    /**
     * Closes the NewAlarmFragment
     */
    private void closeNewAlarmFragment() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(fragmentManager.findFragmentById(R.id.set_alarm_container));
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
        fragmentTransaction.commit();

    }

    /**
     * This method populates the array of alarms that have been set by a user
     */
    private void getAlarms() {

        Log.d("CLOCK_ACTIVITY","getAlarms");

        try {
            String fileName = getFilesDir() + "/alarms";
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
     * Saving alarms upon application exit
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("CLOCK_ACTIVITY","onDestroy");
//        try{
//            String fileName = getFilesDir() + "/alarms";
//            FileOutputStream fos = this.openFileOutput(fileName, this.MODE_PRIVATE);
//            ObjectOutputStream os = new ObjectOutputStream(fos);
//            os.writeObject(alarmList);
//            os.close();
//            fos.close();
//        } catch(IOException e) {
//            e.printStackTrace();
//            System.err.println("Error saving alarms");
//        }

    }
}
