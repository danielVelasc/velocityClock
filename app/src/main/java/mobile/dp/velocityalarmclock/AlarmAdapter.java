package mobile.dp.velocityalarmclock;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

/**
 * @author Daniel Velasco
 * @since February 05, 2017
 *
 * The goal of this class is to create the alarm layouts and handle
 * events such as setting an alarm as active/inactive and deleting existing alarms
 *
 */

public class AlarmAdapter extends BaseAdapter implements ListAdapter {

    private Context context;
    

    public AlarmAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }
}
