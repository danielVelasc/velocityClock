package mobile.dp.velocityalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.UUID;

/**
 * @author Daniel Velasco
 * @since February 13, 2017
 * @version 1.0
 *
 * This class will manage all alarms set by the user.
 * It is the central point for alarm:
 *      - creation
 *      - deletion
 *      - modification
 *      - aggregation (i.e. alarms will be maintained here)
 *      - serialization/deserialization
 * Classes interested in keeping track of alarm updates should subscribe by calling the
 * register method and implementing the corresponding interface.
 */

class AlarmCoordinator {
    private static final String TAG = "ALARM_COORDINATOR";
    private static final String ALARM_LIST_FILE_NAME = "alarm-list";

    private HashMap<Alarm, PendingIntent> scheduledIntents;
    private ArrayList<Alarm> alarmList;
    private ArrayList<AlarmCoordinatorListener> listeners;

    private static final AlarmCoordinator instance = new AlarmCoordinator();
    private MediaPlayer mPlayer;

    private AlarmCoordinator () {
        scheduledIntents = new HashMap<>();
        alarmList = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    protected static AlarmCoordinator getInstance ()
    { return instance; }

    /**
     * Schedules a new alarm in the system services. Additionally adds alarms to be kept track of.
     * @param context
     * @param alarm
     */
    void createNewAlarm(Context context, Alarm alarm) {

        Intent alertIntent = new Intent(context, AlarmReceiver.class); // When timer ends, check with receiver
        alertIntent.putExtra("Alarm-Name", alarm.getName());
        alertIntent.putExtra("Alarm-ID", alarm.getUuid()); // Will be helpful later

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        PendingIntent scheduledIntent = PendingIntent.getBroadcast(context, IDGenerator.getID(), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT); //Generate pending intent
        switch (alarm.getAlarmFrequency()) {
            case NO_REPEAT:
                alarmMgr.set(AlarmManager.RTC_WAKEUP, alarm.getTimeToGoOff(), scheduledIntent); // Don't repeat
                break;
            case DAILY_REPEAT:
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTimeToGoOff(), 24 * 60 * 60 * 1000, scheduledIntent); //Repeats every 24 hours after
                break;
            case WEEKLY_REPEAT:
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarm.getTimeToGoOff(), 24 * 60 * 60 * 1000 * 7, scheduledIntent); //Repeats every 7 days hours after
        }

        scheduledIntents.put(alarm, scheduledIntent); //Add to list of Scheduledintents

        alarm.setState(true);
        alarmList.add(alarm);
        notifyAlarmChange();


    }

    void deleteAlarm(int i, Context passedContext) {
        deleteAlarm(alarmList.get(i), passedContext);
    }

    /**
     * First removes the alarm from the alarmList via its ID, then tells the AlarmManager to cancel it via
     * the alarm's scheduledIntent memeber variable. Finally, broadcasts a message to all listeners to do the
     * same.
     * @param alarm the Alarm to be deleted
     */
    void deleteAlarm(Alarm alarm, Context passedContext) {
        AlarmManager alarmMgr = (AlarmManager) passedContext.getSystemService(Context.ALARM_SERVICE);

        // 1. Remove the alarm from AlarmCoordinator's alarmList
        for(int position = 1; position < alarmList.size(); position++){
            if(alarmList.get(position).getUuid() == alarm.getUuid()){
                alarmList.remove(position);
                break;
            }
        }

        // 2. Tell the AlarmManager to cancel the alarm
        alarmMgr.cancel(scheduledIntents.get(alarm));
        scheduledIntents.remove(alarm);

        // 3. Broadcast a cancel to all registered Listeners
        notifyAlarmChange();
    }

    /**
     * Snoozes an alarm
     * @param context The calling context
     * @param alarm The alarm to snooze
     */
    public void snoozeAlarm(Context context, Alarm alarm) {
        stopAlarmNoise(context); //Stop the alarm noise.
        scheduleNextSnooze(context, alarm);
    }

    /**
     * Schedule System alarm to go down in the alarm's snooze time
     * @param context Calling context
     * @param alarm Alarm that was snoozed
     */
    private void scheduleNextSnooze(Context context, Alarm alarm) {

        Intent alertIntent = new Intent(context, AlarmReceiver.class); //When timer ends, check with receiver
        alertIntent.putExtra("Alarm-Name", alarm.getName() + " - Snoozed");
        alertIntent.putExtra("Alarm-ID", alarm.getUuid()); // Will be helpful later

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); //Schedule alarm to go off in the snooze time
        PendingIntent scheduledIntent = PendingIntent.getBroadcast(context, IDGenerator.getID(), alertIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + alarm.getSnoozeTime(), scheduledIntent);
        scheduledIntents.put(alarm, scheduledIntent);

    }

    /**
     * Dismiss an alarm
     * @param context Calling context
     * @param alarm Alarm that was dismissed
     */
    public void dismissAlarm(Context context, Alarm alarm) {
        stopAlarmNoise(context);
        alarm.setState(false);
    }

    void notifyAlarmChange() {
        for(AlarmCoordinatorListener listener: listeners)
            listener.alarmChanged();
    }

    void registerListener(AlarmCoordinatorListener listener) {
        listeners.add(listener);
    }

    ArrayList<Alarm> getAlarmList() {
        return alarmList;
    }

    //TODO Add method to serialize alarms
    /**
     * Saves alarmList to file in internal storage.
     * @param context Calling context (activity)
     */
    public void saveAlarmList(Context context) {
        ObjectOutputStream outputStream;

        try {
            outputStream =  new ObjectOutputStream(context.openFileOutput(ALARM_LIST_FILE_NAME, Context.MODE_PRIVATE));

            if (alarmList == null) {
                outputStream.writeObject(new ArrayList<Alarm>());
            } else {
                outputStream.writeObject(alarmList);
            }

            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads file containing alarms in internal storage into alarmList.
     * @param context Calling context (activity)
     */
    public void loadAlarmList(Context context) {
        ObjectInputStream inputStream;

        try {
            inputStream = new ObjectInputStream(context.openFileInput(ALARM_LIST_FILE_NAME));
            Object list = inputStream.readObject();

            if (list != null && ((ArrayList<Alarm>) list).size() > 0) {
                alarmList = (ArrayList<Alarm>) list;
                Log.d(TAG, "loaded alarm list with " + alarmList.size() + " alarms");
            } else {
                // Add an empty alarm at the start that will never be referenced.
                alarmList.add(new Alarm());
                notifyAlarmChange();
                Log.d(TAG, "adding null alarm");
            }

            inputStream.close();
        } catch (FileNotFoundException e) {
            alarmList.add(new Alarm());
            notifyAlarmChange();
            Log.d(TAG, "adding null alarm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Will play the alarm noise on repeat until stopAlarmNoise is called
     * @param context Calling context
     */
    public void playAlarmNoise(Context context) {
        //activating looping ringtone sound
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mPlayer = MediaPlayer.create(context, notification);
        mPlayer.setLooping(true);
        mPlayer.start();
    }

    /**
     * Stops playing the alarm ringing
     * @param context Calling context
     */
    public void stopAlarmNoise(Context context) {
        if ((mPlayer != null) && (mPlayer.isPlaying())) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * Obtains the Alarm with a given UUID
     * @param UUID The UUID of the alarm to request
     * @return The alarm with specified UUID
     * @throws NoSuchElementException if the alarm could not be found
     */
    public Alarm getAlarmByID(String UUID) {
        for (int i = 1; i < alarmList.size(); i++) {
            if (alarmList.get(i).getUuid().equalsIgnoreCase(UUID)) return alarmList.get(i);
        }
        throw new NoSuchElementException("The alarm by the UUID " + UUID + " could not be found");
    }
}

