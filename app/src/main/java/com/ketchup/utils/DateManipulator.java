package com.ketchup.utils;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/** Date 값을 받아서 초기화한 후 Calendar 값을 조작하여 반환하는 클래스 */
public class DateManipulator {
    public static final String DATE_FORMAT = "yyyy.MM.dd, EEE HH:mm aa";

    public static final String DATE_FORMAT_24HOUR = "k:mm";
    public static final String DATE_FORMAT_12HOUR_AM_PM = "h:mm aa";
    public static final String DATE_FORMAT_DATE_PICKER = "yyyy. MM. dd";

    public static final int IT_IS_TODAY = 0;
    public static final int IT_IS_FUTURE = 1;
    public static final int IT_IS_PAST = -1;

    private Date currentTime;
    private Locale locale;
    private Calendar calendar;
    private SimpleDateFormat simpleDateFormat;


    public DateManipulator(Date date, Locale locale) {
        this.currentTime = (date == null ? new Date() : date);
        this.locale = locale;

        this.calendar = Calendar.getInstance();
        this.calendar.setTime(currentTime);

        this.simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, locale);
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }

    public String getDateString() {
        return simpleDateFormat.format(currentTime);
    }

    public String getDateString(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, locale);
        return simpleDateFormat.format(date);
    }

    public String getDateString(Date date, String dateFormat) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat, locale);
        return simpleDateFormat.format(date);
    }

    public String get24HourFormatString(Context context) {
        if (DateFormat.is24HourFormat(context))
            return DATE_FORMAT_24HOUR;

        return DATE_FORMAT_12HOUR_AM_PM;
    }

    public Date getTime() {
        return calendar.getTime();
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    // Caution : MONTH + 1
    public int getMonth() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int getDate() {
        return calendar.get(Calendar.DATE);
    }

    public int getHour() {
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    public int getMinute() {
        return calendar.get(Calendar.MINUTE);
    }

    public String getDayOfWeek() {
        String day = "";

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        switch (dayOfWeek) {
            case 1:
                day = "일";
                break;
            case 2:
                day = "월";
                break;
            case 3:
                day = "화";
                break;
            case 4:
                day = "수";
                break;
            case 5:
                day = "목";
                break;
            case 6:
                day = "금";
                break;
            case 7:
                day = "토";
                break;
        }
        return day;
    }

    public Calendar setTodayPlus1Hour(Context context) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DATE, getDate());

        if (DateFormat.is24HourFormat(context)) {
            cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
        } else {
            cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + 1);
        }
        setCalendar(cal);
        return cal;
    }

    public boolean isToday(Calendar now, Calendar target) {
        return ( (now.get(Calendar.YEAR) == target.get(Calendar.YEAR))
                && (now.get(Calendar.MONTH) == target.get(Calendar.MONTH))
                && (now.get(Calendar.DATE) == target.get(Calendar.DATE)));
    }

    public int compareCalendar(Calendar now, Calendar target) {
        if (isToday(now, target)) {
            if (target.before(now))
                return IT_IS_PAST;
            return IT_IS_TODAY; //0
        }
        else if (target.before(now))
            return IT_IS_PAST;  // -1
        else    // target.after(now)
            return IT_IS_FUTURE;    // 1
    }

    public Date setDate(Calendar cal, Date dueDate, int year, int month, int day) {
        cal.setTime(dueDate);
        cal.set(year, month, day);
        return cal.getTime();
    }

    public Date setTime(Calendar cal, Date dueDate, int hour, int minute) {
        cal.setTime(dueDate);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DATE), hour, minute, 0);
        return cal.getTime();
    }
}
