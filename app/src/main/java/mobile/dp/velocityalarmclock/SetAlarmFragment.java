package mobile.dp.velocityalarmclock;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author Sharon Wang
 * @since February 4, 2017
 *
 * This class allows a user to set an alarm.
 */

public class SetAlarmFragment extends Fragment {

    SetAlarmFragmentListener mListener;
    View v;
    int day, hour, minutes;
    Date time;
    Button setAlarmButton;
    TimePicker setAlarmTime;
    Spinner daySpin; // Change to list with checkboxes (multi-selection)

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (v == null){
            v = inflater.inflate(R.layout.set_alarm_fragment, container, false);

            setAlarmButton = (Button) v.findViewById(R.id.setAlarmButton);
            setAlarmTime = (TimePicker) v.findViewById(R.id.setAlarmTime);
            daySpin = (Spinner) v.findViewById(R.id.daySpin); // Change to list with checkboxes (multi-selection)

            // Grab day, hour and minutes when setAlarmButton pressed
            setAlarmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    day = dayToInt(daySpin.getSelectedItem().toString());

                    if (Build.VERSION.SDK_INT >= 23 ){
                        hour = setAlarmTime.getHour();
                        minutes = setAlarmTime.getMinute();
                    }
                    // Deprecated methods, but are necessary for SDK < 23
                    else{
                        hour = setAlarmTime.getCurrentHour();
                        minutes = setAlarmTime.getCurrentMinute();
                    }

                    //TODO: Add day of week
                    Calendar cal = Calendar.getInstance(); //Create a calendar with the time at which to set off the alarm
                    cal.setTimeInMillis(System.currentTimeMillis()); //Current time (for year, month etc)
                    cal.set(Calendar.HOUR_OF_DAY, hour); //Reset other time attributes to relevant time, ie when to go off.
                    cal.set(Calendar.MINUTE, minutes);
                    cal.set(Calendar.SECOND, 0);

                    Toast.makeText(getActivity(), "Day: " + day + "\nTime: " + hour + ":" + minutes, Toast.LENGTH_SHORT).show();
                    Alarm newAlarm = new Alarm(day, cal.getTime(), false);

                    mListener.submitNewAlarm(newAlarm);
                }
            });

        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SetAlarmFragmentListener) {
            mListener = (SetAlarmFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public int dayToInt(String convertDay){
        HashMap<String, Integer> convertMap = new HashMap<String, Integer>();
            convertMap.put("Sunday",1);
            convertMap.put("Monday",2);
            convertMap.put("Tuesday",3);
            convertMap.put("Wednesday",4);
            convertMap.put("Thursday",5);
            convertMap.put("Friday",6);
            convertMap.put("Saturday",7);

        return convertMap.get(convertDay).intValue();
    }
}


