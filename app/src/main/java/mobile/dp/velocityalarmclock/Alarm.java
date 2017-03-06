package mobile.dp.velocityalarmclock;

import android.app.PendingIntent;
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
public class Alarm implements Serializable {

    private String name; //Name of the alarm (we may or may not want this)
    private String uuid; //The unique alarm id
    private int pendingIntentID;
    private int dayOfWeek, hourOfDay, minOfHour;
    private Date time;
    private long snoozeTime = 60 * 1000;
    private boolean repeat;
    private boolean isActive = true;

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

        this (dayOfWeek, time, repeat, "Alarm");

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
        this.name = name;
        Log.d(getClass().getName(), "Day " + dayOfWeek + "Hour " + hourOfDay + "Minute " + minOfHour);

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
}