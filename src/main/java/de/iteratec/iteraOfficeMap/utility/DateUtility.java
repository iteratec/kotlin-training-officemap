package de.iteratec.iteraOfficeMap.utility;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import static java.util.Locale.GERMANY;
import static java.util.TimeZone.getTimeZone;

public class DateUtility {

    private static final Locale locale1 = GERMANY;
    private static final TimeZone tz1 = getTimeZone("Europe/Berlin");

    public static Long startOfDay(Date date) {
        //Start at 0am and end at 23:59:59:999pm
        // Get Calendar object set to the date and time of the given Date object
        Calendar cal = Calendar.getInstance(tz1, locale1);
        cal.setTime(date);

        // Set time fields to zero
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);


        return cal.getTime().getTime();
    }

    public static Long endOfDay(Date date) {
        //Start at 0am and end at 23:59:59:999pm
        // Get Calendar object set to the date and time of the given Date object
        Calendar cal = Calendar.getInstance(tz1, locale1);
        cal.setTime(date);

        // Set time fields to max
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);


        return cal.getTime().getTime();
    }
}
