package com.noteshareapp.noteshare;

import android.content.ContentValues;
import android.provider.CalendarContract;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Jay on 18-11-2015.
 */
public class CalendarEvent {
    private String title;
    private String descr;
    private String location;
    private long startTime;
    private long endTime;
    private String idCalendar;

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getIdCalendar() {
        return idCalendar;
    }

    public void setIdCalendar(String idCalendar) {
        this.idCalendar = idCalendar;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public static ContentValues toICSContentValues(CalendarEvent evt) {

        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events.CALENDAR_ID, evt.getIdCalendar());
        cv.put(CalendarContract.Events.TITLE, evt.getTitle());
        cv.put(CalendarContract.Events.DESCRIPTION, evt.getDescr());
        //cv.put(CalendarContract.Events.EVENT_LOCATION, evt.getLocation());
        cv.put(CalendarContract.Events.DTSTART, evt.getStartTime());
        cv.put(CalendarContract.Events.DTEND, evt.getEndTime());
        cv.put(CalendarContract.Events.HAS_ALARM, true);
        cv.put(CalendarContract.Events.EVENT_COLOR, 16734816);

        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        cv.put(CalendarContract.Events.EVENT_TIMEZONE, tz.getDisplayName());
        /*
        cv.put(Events.STATUS, 1);
        cv.put(Events.VISIBLE, 0);
        cv.put("transparency", 0);
        */

        return cv;
    }

    public static ContentValues setReminder(Long eventID){
        ContentValues reminders = new ContentValues();
        reminders.put(CalendarContract.Reminders.EVENT_ID, eventID);
        reminders.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminders.put(CalendarContract.Reminders.MINUTES, 0);

        return  reminders;
    }
}
