
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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

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
    Button setAlarmButton, cancelButton;
    TimePicker setAlarmTime;
    Spinner daySpin; // TODO - Change spinner to list with checkboxes (multi-selection)

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (v == null){
            v = inflater.inflate(R.layout.set_alarm_fragment, container, false);

            Log.d("SetAlarmFragment","onCreateView");
            setAlarmButton = (Button) v.findViewById(R.id.setAlarmButton);
            cancelButton = (Button) v.findViewById(R.id.cancelButton);
            setAlarmTime = (TimePicker) v.findViewById(R.id.setAlarmTime);
            daySpin = (Spinner) v.findViewById(R.id.daySpin); // TODO - Change spinner to list with checkboxes (multi-selection)

            // Cancel setting the alarm and return back to the main alarm view
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.cancelSetAlarm();
                    Toast.makeText(getActivity(), "alarm cancelled!", Toast.LENGTH_SHORT).show();
                }
            });

            // Grab day, hour and minutes when setAlarmButton pressed
            setAlarmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    day = dayToInt(daySpin.getSelectedItem().toString()); // TODO - Change spinner to list with checkboxes (multi-selection)

                    if (Build.VERSION.SDK_INT >= 23 ){
                        hour = setAlarmTime.getHour();
                        minutes = setAlarmTime.getMinute();
                    }
                    // Deprecated methods, but are necessary for SDK < 23
                    else{
                        hour = setAlarmTime.getCurrentHour();   // getCurrentHour
                        minutes = setAlarmTime.getCurrentMinute();  // getCurrentMinute
                    }

                    //TODO: Add day of week
                    Calendar cal = Calendar.getInstance(); //Create a calendar with the time at which to set off the alarm
                    cal.setTimeInMillis(System.currentTimeMillis()); //Current time (for year, month etc)
                    cal.set(Calendar.HOUR_OF_DAY, hour); //Reset other time attributes to relevant time, ie when to go off.
                    cal.set(Calendar.MINUTE, minutes);
                    cal.set(Calendar.SECOND, 0);

<<<<<<< HEAD
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                    try {
                        time = simpleDateFormat.parse(timeString);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
=======
                    Toast.makeText(getActivity(), "Day: " + day + "\nTime: " + hour + ":" + minutes, Toast.LENGTH_SHORT).show();
                    Alarm newAlarm = new Alarm(day, cal.getTime(), false);
>>>>>>> Alarm

                    mListener.submitNewAlarm(newAlarm);
                    Toast.makeText(getActivity(), "alarm set!", Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "day: " + day + "\ntime: " + hour + ":" + minutes, Toast.LENGTH_SHORT).show();
                }
            });

        }
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("SetAlarmFragment","onAttach");
        if (context instanceof SetAlarmFragmentListener) {
            mListener = (SetAlarmFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    // Converts the String format of a day into an Integer
    public int dayToInt(String convertDay){
        Log.d("SetAlarmFragment","dayToInt");
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

    // Call the cancelSetAlarm method in the main activity
    public void cancelSet(){
        mListener.cancelSetAlarm();
    }
}