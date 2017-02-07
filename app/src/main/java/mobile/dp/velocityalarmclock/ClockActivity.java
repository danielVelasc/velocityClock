package mobile.dp.velocityalarmclock;

import android.app.AlarmManager;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
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

    //NewAlarmFragment createNewAlarmFragment;
    ArrayList<Alarm> alarmList = new ArrayList<Alarm>();
    SetAlarmFragment setAlarmFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        Log.d("CLOCK_ACTIVITY","onCreate");

        //TODO: Deserialize the alarms using function
        //getAlarms();

        // Test alarms
        alarmList.add(new Alarm(1, new Date(1000000), true));
        alarmList.add(new Alarm(2, new Date(2000000), false));
        alarmList.add(new Alarm(3, new Date(3000000), true));
        alarmList.add(new Alarm(4, new Date(4000000), true));


        AlarmAdapter alarmAdapter = new AlarmAdapter(this, alarmList);
        ListView alarmListView = (ListView)findViewById(R.id.alarmListView);
        fixListView(alarmListView);
        alarmListView.setAdapter(alarmAdapter);

    }

    private void fixListView(ListView alarmView)
    {
        alarmView.setOnTouchListener(new ListView.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disallow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        // Allow ScrollView to intercept touch events.
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Handle ListView touch events.
                v.onTouchEvent(event);
                return true;
            }
        });
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

        Calendar cal = Calendar.getInstance(); //Create a calendar with the time at which to set off the alarm
        cal.setTimeInMillis(System.currentTimeMillis()); //Current time (for year, month etc)
        cal.set(Calendar.HOUR_OF_DAY, alarm.getHourOfDay()); //Reset other time attributes to relevant time, ie when to go off.
        cal.set(Calendar.MINUTE, alarm.getMinOfHour());
        cal.set(Calendar.SECOND, alarm.getSecOfMin());

        Intent alertIntent = new Intent(this, AlarmReceiver.class);
        alertIntent.putExtra("Alarm-Name", alarm.getName());
        alertIntent.putExtra("Alarm-ID", alarm.getUuid()); //Will probably be helpful later

        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (alarm.repeats()) //Schedule alarm to repeat if necessary
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 1000 * 3600 * 24, PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        else
            alarmMgr.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), PendingIntent.getBroadcast(this, 1, alertIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        alarm.setState(true);

        closeNewAlarmFragment();
    }


    /**
     * Closes the NewAlarmFragment
     */
    private void closeNewAlarmFragment() {

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

       // fragmentTransaction.remove(createNewAlarmFragment);
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

        // TODO: move functionality to an alarm manager
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
