package mobile.dp.velocityalarmclock.GUIManagement;

import android.content.Context;
import android.graphics.Point;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import mobile.dp.velocityalarmclock.Alarm;
import mobile.dp.velocityalarmclock.AlarmManagement.AlarmCoordinatorListener;
import mobile.dp.velocityalarmclock.AlarmManagement.AlarmCoordinator;
import mobile.dp.velocityalarmclock.R;

/**
 * @author Daniel Velasco
 * @since February 05, 2017
 * @version 1.0
 *
 * The goal of this class is to create the alarm layouts and handle
 * events such as turning an alarm as active/inactive and deleting existing alarms
 *
 */
public class AlarmAdapter extends ArrayAdapter<Alarm> implements AlarmCoordinatorListener {
    public final static String TAG = "ALARM_ADAPTER";

    List<Alarm> alarmList;
    AlarmAdapterListener mListener;

    /**
     * Basic Constructor for AlarmAdapter that initializes member variables
     * @param context
     * @param resource
     * @param items
     */
    public AlarmAdapter(Context context, int resource, List<Alarm> items) {
        super(context, resource, items);

        alarmList = items;
        mListener = (AlarmAdapterListener)context;
    }

    /**
     * @return The number of elements in the alarmList
     */
    @Override
    public int getCount() {
        return alarmList.size();
    }

    @Override
    public int getViewTypeCount() { return 2; }

    @Override
    public int getItemViewType(int position)
    {
        if(position == 0)
            return 0;
        else
            return 1;
    }

    @Override
    public Alarm getItem(int i) {
        return alarmList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    /**
     * Updates the view at a specified position and creates a new view if it null
     * Handles the case of clock_element (position == 0) and single_alarm_element (position > 0)
     *
     * @param position The position in the list
     * @param convertView The view associated with the list item
     * @param viewGroup The parent ViewGroup
     * @return The updated view
     */
    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        List<Alarm> alarmList = AlarmCoordinator.getInstance().getAlarmList();

        View view = convertView;

        // If view is null, we must inflate a view depending on its position.
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // The clock view is placed at position 0
            if (position == 0) {
                view = inflater.inflate(R.layout.clock_element, null);

                String date = new SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                TextView weekday_month_day = (TextView) view.findViewById(R.id.dateTextView);
                weekday_month_day.setText(date);

                // Get size of screen
                Display display = ((AppCompatActivity)getContext()).getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                // Get view to resize and layout parameters for that view
                RelativeLayout clockLayout = (RelativeLayout) view.findViewById(R.id.clockRelativeLayout);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, size.y);
                clockLayout.setLayoutParams(params);
            }
            // Alarm views are placed at all positions > 0
            else {
                view = inflater.inflate(R.layout.single_alarm_element, null);

                // Set listener for switch
                SwitchCompat activeStatusSwitch = (SwitchCompat)view.findViewById(R.id.alarmSwitch);
                activeStatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int position = (Integer) buttonView.getTag();
                        //alarmList.get(position).setState(isChecked);
                        AlarmCoordinator.getInstance().getAlarmList().get(position).setState(isChecked);
                    }
                });

                // Set listener for delete button
                FloatingActionButton deleteButton = (FloatingActionButton)view.findViewById(R.id.deleteButton);
                deleteButton.setOnClickListener(new FloatingActionButton.OnClickListener() {
                    // This method alerts the AlarmCoordinator that the alarm at position is to be deleted
                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();
                        deleteItem(position);
                    }
                });

                view.setOnTouchListener(new View.OnTouchListener() {
                    private final long tapTime = 200;
                    private long downTime = 0;
                    private boolean tapCancelled = true;

                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getActionMasked()) {
                            case (MotionEvent.ACTION_DOWN):
                                // Log.d(TAG, "Action was DOWN");
                                downTime = event.getEventTime();
                                tapCancelled = false;
                                return true;
                            case (MotionEvent.ACTION_MOVE):
                                // Log.d(TAG, "Action was MOVE");
                                tapCancelled = true;
                                return true;
                            case (MotionEvent.ACTION_UP):
                                // Log.d(TAG, "Action was UP");
                                long upTime = event.getEventTime();
                                if(!tapCancelled && (upTime-downTime)<tapTime) {
                                    // Log.d(TAG,"Action was TAP");
                                    int position = (Integer) v.getTag();
                                    mListener.alarmViewTapped(position);
                                }
                                return true;
                            case (MotionEvent.ACTION_CANCEL):
                                // Log.d(TAG, "Action was CANCEL");
                                tapCancelled = true;
                                return true;
                            case (MotionEvent.ACTION_OUTSIDE):
                                // Log.d(TAG, "Movement occurred outside bounds " +
                                //        "of current screen element");
                                tapCancelled = true;
                                return true;
                            default:
                                return true;
                        }
                    }
                });
            }
        }

        // Alarm view exists so now we must update its fields.
        if (position > 0) {
            // Log.d(TAG, "updating position " + position + ". alarmList size: " + alarmList.size());
            // Handle TextView and display string from your list
            TextView alarmTimeText = (TextView)view.findViewById(R.id.alarmTime);
            alarmTimeText.setText(new SimpleDateFormat("h:mm a").format(alarmList.get(position).getTime()));

            // TODO: Show frequency of alarm
//              TextView alarmFrequencyText = (TextView)view.findViewById(R.id.alarmFrequency);
//              alarmFrequencyText.setText(alarmList.get(position).getFrequency());

            // Cache view position on main view with tag
            view.setTag(position);

            // Handle switch (setting on/off)
            SwitchCompat activeStatusSwitch = (SwitchCompat)view.findViewById(R.id.alarmSwitch);
            // Cache view position in status switch with tag
            activeStatusSwitch.setTag(position) ;
            activeStatusSwitch.setChecked(alarmList.get(position).isActive());

            FloatingActionButton deleteButton = (FloatingActionButton)view.findViewById(R.id.deleteButton);
            // Cache view position in button with tag
            deleteButton.setTag(position);

            // TODO: replace alarmRepeatingView with days buttons
            // update alarm repeating label
            TextView alarmRepeatingView = (TextView) view.findViewById(R.id.alarmRepeating);
            boolean[] onDaysList = alarmList.get(position).getDaysOfWeek();
            String[] daysList = getContext().getResources().getStringArray(R.array.days);
            String daysString = "";

            for (int i = 0; i < 7; i++) {
                if (!onDaysList[i])
                    continue;
                daysString += daysList[i+1].substring(0,3) + " ";
            }

            switch (alarmList.get(position).getAlarmFrequency()) {
                case DAILY_REPEAT:
                    alarmRepeatingView.setText("Daily");
                    break;
                case WEEKLY_REPEAT:
                    alarmRepeatingView.setText(daysString);
                    break;
                case NO_REPEAT:
                    alarmRepeatingView.setText(daysString);
                    break;
            }
            // alarmRepeatingView.setText(Alarm.ALARM_FREQUENCY_TO_STRING[alarmList.get(position).getAlarmFrequency().ordinal()]);

            // update alarm name label
            TextView alarmNameView = (TextView) view.findViewById(R.id.alarmName);
            alarmNameView.setText(alarmList.get(position).getName());
        }

        return view;
    }

    /**
     * Called when an alarm changes
     */
    public void alarmChanged() {
        notifyDataSetChanged();
    }

    /**
     * Deletes the alarm in alarmList at index i
     * @param i
     */
    public void deleteItem(int i) {
        AlarmCoordinator.getInstance().deleteAlarm(i, getContext());
        notifyDataSetChanged();
    }
}
