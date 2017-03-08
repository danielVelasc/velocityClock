package mobile.dp.velocityalarmclock;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;
import java.util.UUID;
import java.util.Date;

/**
 * Implementation of an Alarm and its attributes
 * @Author Colin Thompson
 * @Version 2.0
 * @Date February 5th 2017
 */
public class Alarm implements Serializable, Parcelable {
    public  static final int DEFAULT_SNOOZE = 60 * 1000;

    private static final long serialVersionUID = 697655753434998385L;

    private String name; //Name of the alarm (we may or may not want this)
    private String uuid; //The unique alarm id
    private int pendingIntentID; // This is the broadcastID of the alarm, should only be used for scheduling
    private int dayOfWeek, hourOfDay, minOfHour;
    private Date time;
    private long snoozeTime = 60 * 1000;
    private AlarmFrequency frequency;
    private boolean isActive = true;

    // Added Parcelable interface methods so that fragments can accept alarm as a Parcelable

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeStringArray(new String[]{name, uuid, frequency.toString()});
        out.writeIntArray(new int[]{pendingIntentID, dayOfWeek, hourOfDay, minOfHour});
        out.writeLongArray(new long[]{time.getTime(), snoozeTime});
        out.writeBooleanArray(new boolean[]{isActive});
    }

    public static final Parcelable.Creator<Alarm> CREATOR
            = new Parcelable.Creator<Alarm>() {
        public Alarm createFromParcel(Parcel in) {
            return new Alarm(in);
        }

        public Alarm[] newArray(int size) {
            return new Alarm[size];
        }
    };

    private Alarm(Parcel in) {
        String[] stringArray = new String[3];
        in.readStringArray(stringArray);
        name = stringArray[0]; uuid = stringArray[1]; frequency = AlarmFrequency.valueOf(stringArray[2]);

        int[] intArray = new int[4];
        in.readIntArray(intArray);
        pendingIntentID = intArray[0]; dayOfWeek = intArray[1]; hourOfDay = intArray[2]; minOfHour = intArray[3];

        long[] longArray = new long[2];
        in.readLongArray(longArray);
        time = new Date(longArray[0]); snoozeTime = longArray[1];

        boolean[] booleanArray = new boolean[1];
        in.readBooleanArray(booleanArray);
        isActive = booleanArray[0];
    }

    public Alarm() {}

    //TODO: Add sound and snooze
    //ToDo: Determine if the dayOfWeek, hourOfDay, minOfHour, secOfMin are redundant if there is also time
    /**
     * Creates an alarm with default name
     *
     * @param dayOfWeek - the day of the week
     * @param hourOfDay - the hour of the day
     * @param minOfHour - the minute of the hour
     * @param frequency - if true repeat more than once
     */
    public Alarm (int dayOfWeek, int hourOfDay, int minOfHour, AlarmFrequency frequency) {

        this(dayOfWeek, hourOfDay, minOfHour, frequency, "Alarm");
    }

    /**
     * Creates an alarm with given name
     *
     * @param dayOfWeek - the day of the week
     * @param hourOfDay - the hour of the day
     * @param minOfHour - the minute of the hour
     * @param frequency - if true repeat more than once
     */
    public Alarm (int dayOfWeek, int hourOfDay, int minOfHour, AlarmFrequency frequency, String name) {

        this.dayOfWeek = dayOfWeek;
        this.hourOfDay = hourOfDay; //Deprecated but it works for now
        this.minOfHour = minOfHour;
        this.frequency = frequency;
        this.uuid = UUID.randomUUID().toString();
        this.name = name.isEmpty() ? "Alarm" : name;

        Log.d(getName(), " Day " + dayOfWeek + " Hour " + hourOfDay + " Minute " + minOfHour);

    }

    /**
     * Sets a new name for a particular alarm
     * @param name - the name to set
     */
    public void setName(String name) {
        //TODO: Check for invalid names
        this.name = name;
    }

    /**
     * Obtains the current day of the week, 1 being sunday and 7 saturday
     * @return An int corresponding to the day of the week
     */
    public int getDayOfWeek() {
        return this.dayOfWeek;
    }

    /**
     * Obtains alarm hour
     * @return the alarm hour
     */
    public int getHourOfDay() {
        return hourOfDay;
    }

    /**
     * Obtains alarm minutes
     * @return the alarm minutes
     */
    public int getMinOfHour() {
        return minOfHour;
    }

    /**
     * Get the name of the alarm
     * @return the name of the alarm
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the Universal Unique Identifier
     * @return the alarm's UUID
     */
    public String getUuid() {
        return this.uuid;
    }

    /**
     * Get the time of the alarm
     * @return the time of the alarm
     */
    public Date getTime() { return this.time; }

    /**
     *
     * @return true if this alarm is active
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Changes the active state of this alarm
     * @param active true if it's active
     */
    public void setState(boolean active) {
        this.isActive = active;
        // TODO: change status with services too

    }

    /**
     * A method for calculating the initial time to set the alarm for.
     * @return
     */
    public long calcInitialAlarmTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minOfHour);
        cal.set(Calendar.SECOND, 0);

        //If the time is earlier in the day, move the time so it goes off at the next correct instance
        Date futureAlarmTime = cal.getTime();
        if (futureAlarmTime.getTime() < System.currentTimeMillis()) {
            if (this.frequency == AlarmFrequency.DAILY_REPEAT) {
                futureAlarmTime = new Date(futureAlarmTime.getTime() + 1000 * 3600 * 24);
            } else { //weekly or non repeat
                futureAlarmTime = new Date(futureAlarmTime.getTime() + 1000 * 3600 * 24 * 7);
            }
        }

        this.time = futureAlarmTime; //Set in case the original alarm time is needed in the future.
        return futureAlarmTime.getTime();

    }

    /**
     * @return The amount of time this alarm will snooze for in milliseconds
     */
    public long getSnoozeTime() {
        return this.snoozeTime;
    }

    /**
     * @param snoozeTime The amount of time this alarm will snooze for in milliseconds
     */
    public void setSnoozetime(long snoozeTime) {
        this.snoozeTime = snoozeTime;
    }

    /**
     * The frequency of the alarm.
     * @return The alarm frequency
     */
    public AlarmFrequency getAlarmFrequency() {
        return this.frequency;
    }

    /**
     * Set the frequency of the alarm
     * @param frequency of the alarm
     */
    public void setAlarmFrequency(AlarmFrequency frequency) {
        this.frequency = frequency;
    }

    /**
     * Alarm frequency types: NO_REPEAT does not repeat, DAILY_REPEAT for repeating every 24 hours,
     * and WEEKLY_REPEAT repeats every 168 hours.
     */
    public enum AlarmFrequency {
        NO_REPEAT, DAILY_REPEAT, WEEKLY_REPEAT
    }

    public boolean alarmFrequencyRepeats() {
        return frequency == AlarmFrequency.DAILY_REPEAT || frequency == AlarmFrequency.WEEKLY_REPEAT;
    }

    /**
     * Getter for pendingIntentID
     * @return the alarm's pendingIntentID
     */
    public int getPendingIntentID(){
        return pendingIntentID;
    }

    /**
     * Setter for pendingIntentID
     * @param newPendingIntentID the new value for pendingIntentID
     */
    public void setPendingIntentID(int newPendingIntentID){
        pendingIntentID = newPendingIntentID;
    }

    /**
     * Simple method that modifies the time fields of this alarm in accordance with modifiedAlarm
     * @param modifiedAlarm the temporary modified alarm that needs to be used to modify the current alarm
     */
    public void modify(Alarm modifiedAlarm){
        dayOfWeek = modifiedAlarm.getDayOfWeek();
        hourOfDay = modifiedAlarm.getHourOfDay();
        minOfHour = modifiedAlarm.getMinOfHour();
        time = modifiedAlarm.getTime();
        // TODO need to modify this once the ENUM is added
        frequency = modifiedAlarm.getAlarmFrequency();
        name = modifiedAlarm.getName();
    }
}

