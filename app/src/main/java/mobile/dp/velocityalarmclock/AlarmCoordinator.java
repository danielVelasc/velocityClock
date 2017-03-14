package mobile.dp.velocityalarmclock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioManager;
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

    private ArrayList<Alarm> alarmList;
    private ArrayList<AlarmCoordinatorListener> listeners;

    private static final AlarmCoordinator instance = new AlarmCoordinator();
    private MediaPlayer mPlayer;

    private AlarmCoordinator () {
        alarmList = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    protected static AlarmCoordinator getInstance ()
    { return instance; }

    /**
     * Schedules a new alarm(s) in the system services. Additionally adds alarms to be kept track of.
     * @param context
     * @param alarm
     */
    void createNewAlarm(Context context, Alarm alarm) {
        createNewAlarm(context, alarm, false);
    }

    /**
     * Schedules a new alarm(s) in the system services. Additionally adds alarms to be kept track of.
     * @param context
     * @param alarm
     */
    void createNewAlarm(Context context, Alarm alarm, boolean appRestart) {

        Intent alertIntent = new Intent(context, AlarmReceiver.class); // When timer ends, check with receiver
        alertIntent.putExtra("Alarm-Name", alarm.getName());

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long[] times = alarm.calcInitialAlarmTime();
        Alarm.AlarmFrequency freq = alarm.getAlarmFrequency();

        // Only give new pendingIntentID if creating new alarm
        if (!appRestart) {
            for (int day = 0; day < 7; day++) { //Create the initial start time for any enabled day of the week
                if (times[day] == -1) continue; //No alarm that day

                int pendingIntentID = IDGenerator.getID(); //Generate the intent ID and set in alarm
                alarm.setPendingIntentID(pendingIntentID, day);
            }
        }

        alertIntent.putExtra("Alarm-ID", alarm.getPendingIntentID()); // Will be helpful later

            PendingIntent scheduledIntent = PendingIntent.getBroadcast(context, IDGenerator.getID(), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT); //Generate pending intent

            if (freq.equals(Alarm.AlarmFrequency.NO_REPEAT)) { //If it doesnt repeat don't schedule reg intent
                alarmMgr.set(AlarmManager.RTC_WAKEUP, times[day], scheduledIntent);
                break; //No need to iterate through the rest of the days
            } else {
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, times[day], 1000 * 60/*24 * 60 * 60 * 1000 * 7*/, scheduledIntent); //Repeats every 7 days hours after
            }

        }

        if (!appRestart) {
            alarm.setState(true);
            alarmList.add(alarm);
            notifyAlarmChange();
        }
    }

    /**
     * This method performs the following operations:
     * 1. Modifies the relevant alarm in the alarmList
     * 2. Reschedules the modified alarm with system services
     * 3. Broadcasts changes to listeners
     * @param i The index of the alarm that is to be modified
     * @param passedContext
     * @param modifiedAlarm the modify fragment makes a temporary alarm object that should passed
     *                      to this method
     */
    void modifyAlarm(int i, Context passedContext, Alarm modifiedAlarm){
        // Step 1: Get the current alarm from the alarmList and modify it in accordance with the modifiedAlarm
        // TODO This might not be the correct way of getting the alarm
        Alarm alarmToBeModified = alarmList.get(i);
        alarmToBeModified.modify(modifiedAlarm);

        // Step 2: Reschedule the alarm with system services; the way this will be done is by writing
        //         over the current alarm that is scheduled by using the same PendingIntentID
        Intent alertIntent = new Intent(passedContext, AlarmReceiver.class); // When timer ends, check with receiver
        alertIntent.putExtra("Alarm-Name", alarmToBeModified.getName());
        alertIntent.putExtra("Alarm-ID", alarmToBeModified.getPendingIntentID()); // Will be helpful later
        AlarmManager alarmMgr = (AlarmManager) passedContext.getSystemService(Context.ALARM_SERVICE);

        // TODO need to refactor all this stuff once the ENUM is added
        if(alarmToBeModified.getAlarmFrequency() == Alarm.AlarmFrequency.DAILY_REPEAT || alarmToBeModified.getAlarmFrequency() == Alarm.AlarmFrequency.WEEKLY_REPEAT){
            PendingIntent scheduledIntent = PendingIntent.getBroadcast(passedContext, alarmToBeModified.getPendingIntentID(), alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, alarmToBeModified.calcInitialAlarmTime(), 24 * 60 * 60 * 1000, scheduledIntent); //Repeats every 24 hours after
        } else {
            PendingIntent scheduledIntent = PendingIntent.getBroadcast(passedContext, alarmToBeModified.getPendingIntentID(), alertIntent, PendingIntent.FLAG_ONE_SHOT);
            alarmMgr.set(AlarmManager.RTC_WAKEUP, alarmToBeModified.calcInitialAlarmTime(), scheduledIntent);
        }

        // Step 3: Broadcast the changes to all listeners
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
    void deleteAlarm(Alarm alarm, Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // 1. Remove the alarm from AlarmCoordinator's alarmList
        for(int position = 1; position < alarmList.size(); position++){
            if(alarmList.get(position).getUuid().equals(alarm.getUuid())){
                alarmList.remove(position);
                break;
            }
        }

        // 2. Tell the AlarmManager to cancel the alarm

        // No longer need

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
        alertIntent.putExtra("Alarm-ID", alarm.getPendingIntentID()); // Will be helpful later

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); //Schedule alarm to go off in the snooze time
        PendingIntent scheduledIntent = PendingIntent.getBroadcast(context, IDGenerator.getID(), alertIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + alarm.getSnoozeTime(), scheduledIntent);
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

    Alarm getAlarm(int position) { return alarmList.get(position); }

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
    /*    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mPlayer = MediaPlayer.create(context, notification); */
        mPlayer = MediaPlayer.create(context, R.raw.alarm_sound);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
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

    /**
     * Obtains the Alarm with a given UUID
     * @param ID The PendingIntent UUID of the alarm to request
     * @return The alarm with specified UUID
     * @throws NoSuchElementException if the alarm could not be found
     */
    public Alarm getAlarmByPendingIntentID(int ID) {
        for (int i = 1; i < alarmList.size(); i++) {
            if (alarmList.get(i).getPendingIntentID() == ID) return alarmList.get(i);
        }
        throw new NoSuchElementException("The alarm by the PendingIntentID " + ID + " could not be found");
    }

    /**
     * This function is called by the onReceive method of the BootReceiver. When called, it
     * should reschedule all of the PendingIntents.
     * @param context Context that is passed from BootReceiver
     */
    public void rescheduleAlarms(Context context){
        loadAlarmList(context);
        for(int i = 1; i < alarmList.size(); i++){
            createNewAlarm(context, alarmList.get(i), true);
        }
    }
}

