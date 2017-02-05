package mobile.dp.velocityalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.Calendar;


/**
 * This class controls the alarm creation activity.
 * @Author Colin Thompson
 * @Version 1.0
 * @Date February 5th 2017
 */
public class NewAlarm extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    /**
     * Enable an alarm.
     * @param alarm
     */
    public void startAlarm(Alarm alarm) {

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

        alarm.enableAlarm();

    }



}
