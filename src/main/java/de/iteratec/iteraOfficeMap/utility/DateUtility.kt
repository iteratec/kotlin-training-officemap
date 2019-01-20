@file:JvmName("DateUtility")

package de.iteratec.iteraOfficeMap.utility

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime

private val germanZoneId = ZoneId.of("Europe/Berlin")

fun Long.millisToGermanLocalDate(): LocalDate {
    return ZonedDateTime.ofInstant(Instant.ofEpochMilli(this), germanZoneId).toLocalDate()
}

fun LocalDate.startOfDayMillis(): Long {
    return atStartOfDay(germanZoneId).toEpochSecond() * 1000
}

fun LocalDate.endOfDayMillis(): Long {
    return atStartOfDay(germanZoneId).plusDays(1).minusSeconds(1).toEpochSecond() * 1000
}
