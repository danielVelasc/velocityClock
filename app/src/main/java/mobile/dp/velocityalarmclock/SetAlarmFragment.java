
package mobile.dp.velocityalarmclock;

import android.app.Fragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

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
    String alarmName;
    Date time;
    Button setAlarmButton, cancelButton;
    TimePicker setAlarmTime;
    // TODO - add field for alarm name
    Spinner daySpin; // TODO - Change spinner to list with checkboxes (multi-selection)
    EditText nameField;
    // http://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (v == null){
            v = inflater.inflate(R.layout.set_alarm_fragment, container, false);

            Log.d("SetAlarmFragment","onCreateView");
            setAlarmButton = (Button) v.findViewById(R.id.setAlarmButton);
            cancelButton = (Button) v.findViewById(R.id.cancelButton);
            setAlarmTime = (TimePicker) v.findViewById(R.id.setAlarmTime);
            daySpin = (Spinner) v.findViewById(R.id.daySpin); // TODO - Change spinner to list with checkboxes (multi-selection)
            nameField = (EditText) v.findViewById(R.id.alarmName);

            // Cancel setting the alarm and return back to the main alarm view
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.closeSetAlarmFragment();
                    nameField.setText("\r\n");
                    Toast.makeText(getActivity(), "alarm cancelled!", Toast.LENGTH_SHORT).show();
                }
            });

            // Grab alarm name, day, hour and minutes when setAlarmButton pressed
            setAlarmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alarmName = nameField.getText().toString();
                    day = dayToInt(daySpin.getSelectedItem().toString()); // TODO - Change spinner to list with checkboxes (multi-selection)

                    // Set hour and minutes of the inputted time
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

                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm", Locale.ENGLISH);

                    
                    // if no alarm name is specified
                    if (alarmName.isEmpty()){
                        Alarm newAlarm = new Alarm(day, cal.getTime(), false);
                        AlarmCoordinator.getInstance().createNewAlarm(getActivity(), newAlarm);
                        Toast.makeText(getActivity(), "alarm set!", Toast.LENGTH_SHORT).show();
                    }

                    // else if an alarm name is specified
                    else{
                        Alarm newAlarm = new Alarm(day, cal.getTime(), false, alarmName);
                        AlarmCoordinator.getInstance().createNewAlarm(getActivity(), newAlarm);
                        Toast.makeText(getActivity(), "alarm set: " + alarmName, Toast.LENGTH_SHORT).show();
                    }

                    mListener.closeSetAlarmFragment();
                    Toast.makeText(getActivity(), "day: " + day + "\ntime: " + hour + ":" + minutes, Toast.LENGTH_SHORT).show();
                }
            });

        }
        return v;
    }

    // Set mListener to SetAlarmFragmentLister
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

    // Hides the toolbar of the main activity when this fragment is active
    // Source: http://stackoverflow.com/questions/29128162/android-hide-toolbar-in-specific-fragment
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity)getActivity()).getSupportActionBar().hide();
    }

    // Shows the toolbar of the main activity when this fragment is inactive
    // Source: http://stackoverflow.com/questions/29128162/android-hide-toolbar-in-specific-fragment
    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
    }
}