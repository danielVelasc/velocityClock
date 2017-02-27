package mobile.dp.velocityalarmclock;

import android.content.Context;
import android.graphics.Point;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

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
    Context context;
    List<Alarm> alarmList;

    public AlarmAdapter(Context context, int resource, List<Alarm> items) {
        super(context, resource, items);

        this.context = context;
        this.alarmList = items;
    }

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

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        View view = convertView;

        // If view is null, we must inflate a view depending on its position.
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // The clock view is placed at position 0
            if (position == 0) {
                view = inflater.inflate(R.layout.clock_element, null);

                String date = new SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                TextView weekday_month_day = (TextView) view.findViewById(R.id.dateTextView);
                weekday_month_day.setText(date);

                // Get size of screen
                Display display = ((AppCompatActivity)context).getWindowManager().getDefaultDisplay();
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
                        alarmList.get(position).setState(isChecked);
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
            }
        }

        // Alarm view exists so now we must update its fields.
        if (position > 0) {
            // Handle TextView and display string from your list
            TextView alarmTimeText = (TextView)view.findViewById(R.id.alarmTime);
            alarmTimeText.setText(new SimpleDateFormat("h:mm a").format(alarmList.get(position).getTime()));

            // TODO: Show frequency of alarm
//              TextView alarmFrequencyText = (TextView)view.findViewById(R.id.alarmFrequency);
//              alarmFrequencyText.setText(alarmList.get(position).getFrequency());

            // Handle switch (setting on/off)
            SwitchCompat activeStatusSwitch = (SwitchCompat)view.findViewById(R.id.alarmSwitch);
            activeStatusSwitch.setChecked(alarmList.get(position).isActive());

            // Cache view position in button with tag
            activeStatusSwitch.setTag(position) ;

            FloatingActionButton deleteButton = (FloatingActionButton)view.findViewById(R.id.deleteButton);
            // Cache view position in button with tag
            deleteButton.setTag(position);
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
        AlarmCoordinator.getInstance().deleteAlarm(i, context);
        notifyDataSetChanged();
    }
}
