package mobile.dp.velocityalarmclock;

import java.util.UUID

public class Alarm {

    private String name; //Name of the alarm (we may or may not want this)
    private String uuid; //The unique alarm id
    private DayOfWeek dayOfWeek;
    private LocalTime time;
    private boolean repeat;

    //TODO: Add sound and snooze

    /**
     * Creates an alarm with default name
     *
     * @param dayOfWeek - the day of the week
     * @param time - the time of the day
     * @param repeat - if true repeat more than once
     */
    public Alarm (DayOfWeek dayOfWeek, LocalTime time, boolean repeat) {

        this (dayOfWeek, time, repeat, "Alarm");

    }

    /**
     * Creates an alarm with given name
     *
     * @param dayOfWeek - the day of the week
     * @param time - the time of the day
     * @param repeat - if true repeat more than once
     */
    public Alarm (DayOfWeek dayOfWeek, LocalTime time, boolean repeat, name) {

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

}