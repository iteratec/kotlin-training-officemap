@file:JvmName("DateUtility")

package de.iteratec.iteraOfficeMap.utility

import java.time.ZonedDateTime
import java.util.*
import java.util.Locale.GERMANY
import java.util.TimeZone.getTimeZone

private val locale1 = GERMANY
private val tz1 = getTimeZone("Europe/Berlin")

fun startOfDay(date: Date): Long {
    //Start at 0am and end at 23:59:59:999pm
    // Get Calendar object set to the date and time of the given Date object
    val cal = Calendar.getInstance(tz1, locale1)
    cal.time = date

    // Set time fields to zero
    cal.set(Calendar.HOUR_OF_DAY, 0)
    cal.set(Calendar.MINUTE, 0)
    cal.set(Calendar.SECOND, 0)
    cal.set(Calendar.MILLISECOND, 0)

    return cal.time.time
}

fun endOfDay(date: Date): Long {
    //Start at 0am and end at 23:59:59:999pm
    // Get Calendar object set to the date and time of the given Date object
    val cal = Calendar.getInstance(tz1, locale1)
    cal.time = date

    // Set time fields to max
    cal.set(Calendar.HOUR_OF_DAY, 23)
    cal.set(Calendar.MINUTE, 59)
    cal.set(Calendar.SECOND, 59)
    cal.set(Calendar.MILLISECOND, 999)

    return cal.time.time
}

fun startOfDay(zonedDateTime: ZonedDateTime): ZonedDateTime {
    return zonedDateTime.toLocalDate().atStartOfDay(zonedDateTime.zone)
}

fun endOfDay(zonedDateTime: ZonedDateTime): ZonedDateTime {
    // TODO: we must remove minusSeconds(1), but then we have to make sure the JavaScript code can handle it
    return startOfDay(zonedDateTime).plusDays(1).minusSeconds(1)
}
