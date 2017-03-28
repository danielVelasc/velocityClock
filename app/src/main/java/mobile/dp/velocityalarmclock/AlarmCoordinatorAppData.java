package mobile.dp.velocityalarmclock;

import android.content.Intent;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Container for data that must be saved and loaded in a permanent location when required by the alarm coordinator.
 *
 * Created by ferna on 2017-03-27.
 */

public class AlarmCoordinatorAppData implements Serializable {
    private static final long serialVersionUID = 591435783475973445L;

    private ArrayList<Alarm> mAlarmList;
    private ArrayList<IntentHolder> mAlarmPendingList;

    public AlarmCoordinatorAppData(ArrayList<Alarm> alarmList, ArrayList<Intent> alarmPendingList) {
        mAlarmList = alarmList;
        mAlarmPendingList = new ArrayList<>();

        for (Intent intent : alarmPendingList) {
            mAlarmPendingList.add(new IntentHolder(intent));
        }
    }

    public ArrayList<Alarm> getAlarmList() {
        return mAlarmList;
    }

    public ArrayList<Intent> getAlarmPendingList() {
        ArrayList<Intent> pendingIntents = new ArrayList<>();

        for (IntentHolder intentHolder : mAlarmPendingList) {
            pendingIntents.add(intentHolder.getIntent());
        }

        return pendingIntents;
    }
}
