package mobile.dp.velocityalarmclock;

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
    private int dayOfWeek, hourOfDay, minOfHour, secOfMin;
    private Date time;
    private boolean repeat;
    private boolean isActive;

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

        this.dayOfWeek = dayOfWeek;
        this.time = time;
        this.repeat = repeat;
        this.uuid = UUID.randomUUID().toString();
        this.name = name;

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
     * Marks an alarm as enabled
     */
    public void enableAlarm() {
        isActive = true;
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
     * Obtains alarm seconds
     * @return the alarm seconds
     */
    public int getSecOfMin() {
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
     * @param state new state
     */
    public void setState(boolean state) {
        this.isActive = state;
        // TODO: change status with services too

    }
}
