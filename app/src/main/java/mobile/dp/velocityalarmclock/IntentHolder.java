package mobile.dp.velocityalarmclock;

import android.content.Intent;

import java.io.Serializable;

/**
 * Serializable object that saves important Intent data. Allows for converting from IntentHolder to Intent
 *
 * Created by ferna on 2017-03-27.
 */

public class IntentHolder implements Serializable {
    private static final long serialVersionUID = 394812374685943687L;

    private boolean mLaunchDialog;
    private String mAlarmName;
    private int mAlarmID;
    private int mFlags;

    public IntentHolder(Intent intent) {
        if (intent != null) {
            mLaunchDialog = intent.getBooleanExtra(AlarmCoordinator.ALARM_LAUNCH_DIALOG, false);
            mAlarmName = intent.getStringExtra(AlarmCoordinator.ALARM_NAME);
            mAlarmID = intent.getIntExtra(AlarmCoordinator.ALARM_ID, -1);
            mFlags = intent.getFlags();
        } else {
            mLaunchDialog = false;
            mAlarmName = "";
            mAlarmID = -1;
        }
    }

    public Intent getIntent() {
        Intent intent = new Intent();
        intent.putExtra(AlarmCoordinator.ALARM_LAUNCH_DIALOG, mLaunchDialog);
        intent.putExtra(AlarmCoordinator.ALARM_NAME, mAlarmName);
        intent.putExtra(AlarmCoordinator.ALARM_ID, mAlarmID);
        intent.setFlags(mFlags);

        return intent;
    }
}
