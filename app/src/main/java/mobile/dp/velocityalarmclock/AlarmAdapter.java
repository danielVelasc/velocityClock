package mobile.dp.velocityalarmclock;

import android.content.Context;
import android.graphics.Point;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * @author Daniel Velasco
 * @since February 05, 2017
 * @version 1.0
 *
 * The goal of this class is to create the alarm layouts and handle
 * events such as turning an alarm as active/inactive and deleting existing alarms
 *
 */
public class AlarmAdapter extends BaseAdapter implements ListAdapter, AlarmCoordinatorListener {


    private ArrayList<Alarm> alarmList;
    private Context context;


    public AlarmAdapter(Context context) {
        this.context = context;
        this.alarmList = AlarmCoordinator.getInstance().getAlarmList();
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
    public Object getItem(int i) {
        return alarmList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup viewGroup) {

        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // Put the clock in this row (with the same height as the device)
            if(position == 0) {
                view = inflater.inflate(R.layout.clock_element, null);
                String date = new SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH).format(Calendar.getInstance().getTime());
                TextView weekday_month_day = (TextView) view.findViewById(R.id.dateTextView);
                weekday_month_day.setText(date);

                Display display = ((AppCompatActivity)context).getWindowManager().getDefaultDisplay();
                Point size = new Point();
                display.getSize(size);

                // Get view to resize and layout parameters for that view
                RelativeLayout clockLayout = (RelativeLayout) view.findViewById(R.id.clockRelativeLayout);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, size.y);
                clockLayout.setLayoutParams(params);
            }
            else // Put single alarms in each row
            {
                view = inflater.inflate(R.layout.single_alarm_element, null);

                //Handle TextView and display string from your list
                TextView alarmTimeText = (TextView)view.findViewById(R.id.alarmTime);
                alarmTimeText.setText(new SimpleDateFormat("h:mm a").format(alarmList.get(position-1).getTime()));

                // TODO: Show frequency of alarm
//              TextView alarmFrequencyText = (TextView)view.findViewById(R.id.alarmFrequency);
//              alarmFrequencyText.setText(alarmList.get(position).getFrequency());

                //Handle switch (setting on/off)
                SwitchCompat activeStatusSwitch = (SwitchCompat)view.findViewById(R.id.alarmSwitch);
                activeStatusSwitch.setChecked(alarmList.get(position-1).isActive());

                activeStatusSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        alarmList.get(position-1).setState(isChecked);
                    }
                });

                FloatingActionButton deleteButton = (FloatingActionButton)view.findViewById(R.id.deleteButton);
                deleteButton.setOnClickListener(new FloatingActionButton.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        alarmList.remove(position-1);
                        //TODO: Cancel system service
                        notifyDataSetChanged();
                    }
                });

            }
        }

        return view;
    }

    public void alarmChanged() {
        notifyDataSetChanged();
    }

}
