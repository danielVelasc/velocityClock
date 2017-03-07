package mobile.dp.velocityalarmclock;

import android.app.PendingIntent;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.UUID;
import java.util.Date;

/**
 * Implementation of an Alarm and its attributes
 * @Author Colin Thompson
 * @Version 2.0
 * @Date February 5th 2017
 */
public class Alarm implements Serializable, Parcelable {
    private static final long serialVersionUID = 697655753434998385L;

    private String name; //Name of the alarm (we may or may not want this)
    private String uuid; //The unique alarm id
    private int pendingIntentID; // This is the broadcastID of the alarm, should only be used for scheduling
    private int dayOfWeek, hourOfDay, minOfHour;
    private Date time;
    private long snoozeTime = 60 * 1000;
    private boolean repeat;
    private boolean isActive = true;

    // Added Parcelable interface methods so that fragments can accept alarm as a Parcelable

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeStringArray(new String[]{name, uuid});
        out.writeIntArray(new int[]{pendingIntentID, dayOfWeek, hourOfDay, minOfHour});
        out.writeLongArray(new long[]{time.getTime(), snoozeTime});
        out.writeBooleanArray(new boolean[]{repeat, isActive});
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
        String[] stringArray = new String[2];
        in.readStringArray(stringArray);
        name = stringArray[0]; uuid = stringArray[1];

        int[] intArray = new int[4];
        in.readIntArray(intArray);
        pendingIntentID = intArray[0]; dayOfWeek = intArray[1]; hourOfDay = intArray[2]; minOfHour = intArray[3];

        long[] longArray = new long[2];
        in.readLongArray(longArray);
        time = new Date(longArray[0]); snoozeTime = longArray[1];

        boolean[] booleanArray = new boolean[2];
        in.readBooleanArray(booleanArray);
        repeat = booleanArray[0]; isActive = booleanArray[1];
    }

    public Alarm() {}

    //TODO: Add sound and snooze
    //ToDo: Determine if the dayOfWeek, hourOfDay, minOfHour, secOfMin are redundant if there is also time
    /**
     * Creates an alarm with default name
     *
     * @param dayOfWeek - the day of the week
     * @param time - the time of the day
     * @param repeat - if true repeat more than once
     */
    public Alarm (int dayOfWeek, Date time, boolean repeat) {
        this(dayOfWeek, time, repeat, "Alarm");
    }

    /**
     * Creates an alarm with given name
     *
     * @param dayOfWeek - the day of the week
     * @param time - the time of the day
     * @param repeat - if true repeat more than once
     */
    public Alarm (int dayOfWeek, Date time, boolean repeat, String name) {
        if (time == null) Log.d(this.getClass().getName(), "Time null");

        this.dayOfWeek = dayOfWeek;
        this.hourOfDay = time.getHours(); //Deprecated but it works for now
        this.minOfHour = time.getMinutes();
        this.time = time;
        this.repeat = repeat;
        this.uuid = UUID.randomUUID().toString();
        this.name = name.isEmpty() ? "Alarm" : name;

        Log.d(getClass().getName(), " Day " + dayOfWeek + " Hour " + hourOfDay + " Minute " + minOfHour);

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
     * If alarm repeats
     * @return true if the alarm is a repeating alarm
     */
    public boolean repeats() {
        return repeat;
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

    public long getTimeToGoOff() {
        return time.getTime();
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
        repeat = modifiedAlarm.repeat;
    }
}