package mobile.dp.velocityalarmclock;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.NoSuchElementException;

/**
 * @author Daniel Velasco
 * @version 1.0
 *          <p>
 *          This class will manage all alarms set by the user.
 *          It is the central point for alarm:
 *          - creation
 *          - deletion
 *          - modification
 *          - aggregation (i.e. alarms will be maintained here)
 *          - serialization/deserialization
 *          Classes interested in keeping track of alarm updates should subscribe by calling the
 *          register method and implementing the corresponding interface.
 * @since February 13, 2017
 */

class AlarmCoordinator {
    public final static String ALARM_LAUNCH_DIALOG = "alarm-launch-dialog";
    public final static String ALARM_NAME = "alarm-name";
    public final static String ALARM_ID = "alarm-id";
    public final static String ALARM_DAY_INDEX = "alarm-day-index";
    public final static String ALARM_PENDING_INTENT = "alarm-pending-intent";

    private static final String TAG = "ALARM_COORDINATOR";
    private static final String ALARM_COORDINATOR_DATA_FILE_NAME = "alarm-coordinator-data";

    private ArrayList<Integer> alarmNotificationList;
    private ArrayList<Intent> alarmPendingList;
    private ArrayList<Alarm> alarmList;
    private ArrayList<AlarmCoordinatorListener> listeners;

    private static final AlarmCoordinator instance = new AlarmCoordinator();
    private MediaPlayer mPlayer;
    private boolean pendingAlarmRunning;

    private AlarmCoordinator() {
        alarmNotificationList = new ArrayList<>();
        alarmPendingList = new ArrayList<>();
        pendingAlarmRunning = false;
        alarmList = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    protected static AlarmCoordinator getInstance() {
        return instance;
    }

    /**
     * Schedules a new alarm(s) in the system services. Additionally adds alarms to be kept track of.
     *
     * @param context
     * @param alarm
     */
    void createNewAlarm(Context context, Alarm alarm) {
        createNewAlarm(context, alarm, false);
    }

    /**
     * Schedules a new alarm(s) in the system services. Additionally adds alarms to be kept track of.
     *
     * @param context
     * @param alarm
     */
    void createNewAlarm(Context context, Alarm alarm, boolean appRestart) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long[] times = alarm.calcInitialAlarmTime();
        Alarm.AlarmFrequency freq = alarm.getAlarmFrequency();

        // Only give new pendingIntentID if creating new alarm
        for (int dayIndex = 0; dayIndex < 7; dayIndex++) { //Create the initial start time for any enabled day of the week
            if (times[dayIndex] == -1) continue; //No alarm that day

            Log.d(TAG, "Created new PendingIntent for day " + dayIndex + ". It is " + Calendar.getInstance().getTimeInMillis() + " and the alarm will go off at " + times[dayIndex]);

            Intent alertIntent = new Intent(context, AlarmReceiver.class); // When timer ends, check with receiver
            alertIntent.putExtra(ALARM_NAME, alarm.getName());
            alertIntent.putExtra(ALARM_ID, alarm.getPendingIntentID()[dayIndex]); // Will be helpful later
            alertIntent.putExtra(ALARM_DAY_INDEX, dayIndex);

            PendingIntent scheduledIntent = PendingIntent.getBroadcast(context, alarm.getPendingIntentID()[dayIndex], alertIntent, PendingIntent.FLAG_UPDATE_CURRENT); //Generate pending intent

            alertIntent.putExtra("Pending-Intent", scheduledIntent);

            if (freq.equals(Alarm.AlarmFrequency.NO_REPEAT)) { //If it doesnt repeat don't schedule reg intent
                alarmMgr.set(AlarmManager.RTC_WAKEUP, times[dayIndex], scheduledIntent);
                break; //No need to iterate through the rest of the days
            } else {
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, times[dayIndex], 1000 * 60/*24 * 60 * 60 * 1000 * 7*/, scheduledIntent); //Repeats every 7 days hours after
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
     *
     * @param i             The index of the alarm that is to be modified
     * @param passedContext
     * @param modifiedAlarm the modify fragment makes a temporary alarm object that should passed
     *                      to this method
     */
    void modifyAlarm(int i, Context passedContext, Alarm modifiedAlarm) {
        // Step 1: Get the current alarm from the alarmList and modify it in accordance with the modifiedAlarm
        // TODO This might not be the correct way of getting the alarm
        Alarm alarmToBeModified = alarmList.get(i);
        alarmToBeModified.modify(modifiedAlarm);

        int[] pendingIntentIDs = alarmToBeModified.getPendingIntentID();
        long[] initialAlarmTime = alarmToBeModified.calcInitialAlarmTime();
        boolean[] daysOfWeek = modifiedAlarm.getDaysOfWeek();

        // Step 2: Reschedule the alarm with system services; the way this will be done is by writing
        //         over the current alarm that is scheduled by using the same PendingIntentID
        for (int dayIndex = 0; dayIndex < 7; dayIndex++) {
            if (!daysOfWeek[dayIndex])
                continue;

            Intent alertIntent = new Intent(passedContext, AlarmReceiver.class); // When timer ends, check with receiver
            alertIntent.putExtra(ALARM_NAME, alarmToBeModified.getName());
            alertIntent.putExtra(ALARM_DAY_INDEX, dayIndex);
            alertIntent.putExtra(ALARM_ID, alarmToBeModified.getPendingIntentID()[dayIndex]); // Will be helpful later
            AlarmManager alarmMgr = (AlarmManager) passedContext.getSystemService(Context.ALARM_SERVICE);

            // TODO need to refactor all this stuff once the ENUM is added
            if (alarmToBeModified.getAlarmFrequency() == Alarm.AlarmFrequency.DAILY_REPEAT || alarmToBeModified.getAlarmFrequency() == Alarm.AlarmFrequency.WEEKLY_REPEAT) {
                PendingIntent scheduledIntent = PendingIntent.getBroadcast(passedContext, pendingIntentIDs[dayIndex], alertIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, initialAlarmTime[dayIndex], 24 * 60 * 60 * 1000, scheduledIntent); //Repeats every 24 hours after
            } else {
                PendingIntent scheduledIntent = PendingIntent.getBroadcast(passedContext, pendingIntentIDs[dayIndex], alertIntent, PendingIntent.FLAG_ONE_SHOT);
                alarmMgr.set(AlarmManager.RTC_WAKEUP, initialAlarmTime[dayIndex], scheduledIntent);
            }

            dayIndex++;
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
     *
     * @param alarm the Alarm to be deleted
     */
    void deleteAlarm(Alarm alarm, Context context) {
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // 1. Remove the alarm from AlarmCoordinator's alarmList
        for (int position = 1; position < alarmList.size(); position++) {
            if (alarmList.get(position).getUuid().equals(alarm.getUuid())) {
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
     *
     * @param context The calling context
     * @param alarm   The alarm to snooze
     */
    public void snoozeAlarm(Context context, Alarm alarm) {
        stopAlarmNoise(context); //Stop the alarm noise.
        scheduleNextSnooze(context, alarm);
        nextPendingAlarm(context);
    }

    /**
     * Schedule System alarm to go down in the alarm's snooze time
     *
     * @param context Calling context
     * @param alarm   Alarm that was snoozed
     */
    private void scheduleNextSnooze(Context context, Alarm alarm) {
        int day = Calendar.getInstance().getTime().getDay();

        Intent alertIntent = new Intent(context, AlarmReceiver.class); //When timer ends, check with receiver
        alertIntent.putExtra(ALARM_NAME, alarm.getName() + " - Snoozed");
        alertIntent.putExtra(ALARM_DAY_INDEX, day);
        alertIntent.putExtra(ALARM_ID, alarm.getPendingIntentID()[day]); // Will be helpful later

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE); //Schedule alarm to go off in the snooze time
        PendingIntent scheduledIntent = PendingIntent.getBroadcast(context, IDGenerator.getID(), alertIntent, PendingIntent.FLAG_ONE_SHOT);
        alarmMgr.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + alarm.getSnoozeTime(), scheduledIntent);
    }

    /**
     * Dismiss an alarm
     *
     * @param context Calling context
     * @param alarm   Alarm that was dismissed
     */
    public void dismissAlarm(Context context, Alarm alarm) {
        Log.d(TAG, "dismissing alarm " + alarm.getName());
        stopAlarmNoise(context);
        alarm.setState(false);
        alarmPendingList.remove(0);
        nextPendingAlarm(context);
    }

    void notifyAlarmChange() {
        for (AlarmCoordinatorListener listener : listeners)
            listener.alarmChanged();
    }

    void registerListener(AlarmCoordinatorListener listener) {
        listeners.add(listener);
    }

    Alarm getAlarm(int position) {
        return alarmList.get(position);
    }

    ArrayList<Alarm> getAlarmList() {
        return alarmList;
    }

    //TODO Add method to serialize alarms

    /**
     * Saves alarmList and alarmPendingList to file in internal storage.
     *
     * @param context Calling context (activity)
     */
    public void saveAlarmData(Context context) {
        ObjectOutputStream alarmDataOutputStream;

        try {
            alarmDataOutputStream = new ObjectOutputStream(context.openFileOutput(ALARM_COORDINATOR_DATA_FILE_NAME, Context.MODE_PRIVATE));

            AlarmCoordinatorAppData alarmCoordinatorAppData = new AlarmCoordinatorAppData(alarmList, alarmPendingList);

            alarmDataOutputStream.writeObject(alarmCoordinatorAppData);

            alarmDataOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads file containing alarms and alarm intents in internal storage into alarmList.
     *
     * @param context Calling context (activity)
     */
    public void loadAlarmData(Context context) {
        ObjectInputStream inputStream;

        try {
            inputStream = new ObjectInputStream(context.openFileInput(ALARM_COORDINATOR_DATA_FILE_NAME));

            AlarmCoordinatorAppData alarmCoordinatorAppData = (AlarmCoordinatorAppData) inputStream.readObject();

            // Retrieve saved alarms and initialize alarmList
            ArrayList<Alarm> list = alarmCoordinatorAppData.getAlarmList();

            if (list.size() > 0) {
                // Assign alarmList to loaded existing alarm list.
                alarmList = list;
                Log.d(TAG, "loaded alarm list with " + alarmList.size() + " alarms");
            } else {
                // Add an empty alarm at the start that will never be referenced.
                alarmList.add(new Alarm());
                notifyAlarmChange();
                Log.d(TAG, "adding null alarm");
            }

            // Retrieve saved alarm intents and initialize alarmPendingList
            ArrayList<Intent> intents = alarmCoordinatorAppData.getAlarmPendingList();
            for (Intent intent : intents) {
                boolean isUnique = true;
                for (Intent pIntent : alarmPendingList) {
                    if (intent.getIntExtra(ALARM_ID, -1) == pIntent.getIntExtra(ALARM_ID, -2)) {
                        isUnique = false;
                        break;
                    }
                }

                if (isUnique) {
                    alarmPendingList.add(intent);
                }
            }

            // Remove pending alarms intents that are no longer associated with an alarm.
            int i = 0;
            while (i < alarmPendingList.size()) {
                Intent intent = alarmPendingList.get(i);
                try {
                    getAlarmByPendingIntentID(intent.getIntExtra(ALARM_DAY_INDEX, 0), intent.getIntExtra(ALARM_ID, -1));
                    i++;
                } catch (NoSuchElementException e) {
                    alarmPendingList.remove(i);
                }
            }

            Log.d(TAG, "loaded alarm pending list with " + alarmPendingList.size() + " intents");

            inputStream.close();
        } catch (FileNotFoundException | EOFException e) {
            alarmList.add(new Alarm());
            notifyAlarmChange();
            Log.d(TAG, "adding null alarm");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Will play the alarm noise on repeat until stopAlarmNoise is called
     *
     * @param context Calling context
     */
    public void playAlarmNoise(Context context) {
        //activating looping ringtone sound
    /*    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        mPlayer = MediaPlayer.create(context, notification); */

        // Stop playing all other alarms.
        stopAlarmNoise(context);

        mPlayer = MediaPlayer.create(context, R.raw.alarm_sound);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setLooping(true);
        mPlayer.start();
    }

    /**
     * Stops playing the alarm ringing
     *
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
     *
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
     *
     * @param ID The PendingIntent UUID of the alarm to request
     * @return The alarm with specified UUID
     * @throws NoSuchElementException if the alarm could not be found
     */
    public Alarm getAlarmByPendingIntentID(int day, int ID) {
        for (int i = 1; i < alarmList.size(); i++) {
            if (alarmList.get(i).getPendingIntentID()[day] == ID) return alarmList.get(i);
        }
        throw new NoSuchElementException("The alarm by the PendingIntentID " + ID + " could not be found");
    }

    /**
     * This function is called by the onReceive method of the BootReceiver. When called, it
     * should reschedule all of the PendingIntents.
     *
     * @param context Context that is passed from BootReceiver
     */
    public void rescheduleAlarms(Context context) {
        loadAlarmData(context);
        for (int i = 1; i < alarmList.size(); i++) {
            createNewAlarm(context, alarmList.get(i), true);
        }
    }

    /**
     * Adds intent to alarmPendingList. These are intents that will be used in the future to ring an alarm.
     *
     * @param intent
     */
    synchronized public void addPendingAlarm(Intent intent) {
        alarmPendingList.add(intent);
        Log.d(TAG, "addPendingAlarm - pending alarm added. new size of pending list is: " + alarmPendingList.size());
    }

    /**
     * Starts the first intent in alarmPendingList ONLY if an alarm is not already running.
     *
     * @param context
     */
    synchronized public void startPendingAlarm(Context context) {
        if (!pendingAlarmRunning) {
            pendingAlarmRunning = true;
            Intent intent = alarmPendingList.get(0);
            Log.d(TAG, "startPendingAlarm - running first pending alarm with id: " + intent.getIntExtra(ALARM_ID, -1) + ". New size of list: " + (alarmPendingList.size() - 1));
            context.startActivity(intent);
        } else {
            Log.d(TAG, "startPendingAlarm - pending alarm already running!");
        }
    }

    /**
     * Start the next intent and don't check if alarms are already running.
     * Only to be called by alarms that are already running when they finish!
     *
     * @param context
     */
    synchronized public void nextPendingAlarm(Context context) {
        if (alarmPendingList.size() > 0) {
            Intent intent = alarmPendingList.get(0);
            Log.d(TAG, "nextPendingAlarm - running next pending alarm with id: " + intent.getIntExtra(ALARM_ID, -1) + ". New size of list: " + (alarmPendingList.size() - 1));
            context.startActivity(intent);
        } else {
            Log.d(TAG, "nextPendingAlarm - no more pending alarms to run.");
            pendingAlarmRunning = false;
        }
    }

    /**
     * Gets the current pending intent ID
     *
     * @return
     */
    public int getCurrentPendingIntentID() {
        if (alarmPendingList.size() > 0) {
            return alarmPendingList.get(0).getIntExtra(ALARM_ID, -1);
        }
        return -1;
    }

    public void addAlarmNotification(int id) {
        Log.d(TAG, "Adding alarm notification");
        alarmNotificationList.add(id);
    }

    public void clearAlarmNotifications(Context context) {
        NotificationManager notManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        for (int id : alarmNotificationList) {
            notManager.cancel(id);
        }

        alarmNotificationList.clear();
    }
}

