package com.woowla.ghd.extensions

import com.woowla.ghd.presentation.app.i18n
import java.time.format.FormatStyle
import kotlin.time.Duration
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone
import java.time.format.DateTimeFormatter as JavaDateTimeFormatter
import java.time.LocalDateTime as JavaLocalDateTime

fun Instant.after(other: Instant) = compareTo(other) > 0

fun Instant.before(other: Instant) = compareTo(other) < 0

/**
 * Format instant
 */
fun Instant.format(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    formatStyle: FormatStyle = FormatStyle.MEDIUM
): String {
    val localDateTime = this.toLocalDateTime(timeZone)

    val javaLocalDateTime = JavaLocalDateTime.of(localDateTime.year, localDateTime.monthNumber, localDateTime.dayOfMonth, localDateTime.hour, localDateTime.minute, localDateTime.second)
    val javaFormatter = JavaDateTimeFormatter.ofLocalizedDateTime(formatStyle)

    return javaLocalDateTime.format(javaFormatter)
}

/**
 * Transform a [Instant] to a human-readable "minutes/hours ago" or date text.
 * This will return the date text formatted if the duration is greater than [maxHours]
 * @param maxHours The maximum hours to be considered as human-readable
 */
fun Instant.toHRString(
    maxHours: Long = 24,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    formatStyle: FormatStyle = FormatStyle.MEDIUM
): String {
    val duration: Duration = Clock.System.now() - this

    val minutes = duration.inWholeMinutes
    val hours = duration.inWholeHours

    return if (minutes < 60) {
        i18n.generic_minutes_ago(minutes)
    } else if (hours < maxHours) {
        i18n.generic_hours_ago(hours)
    } else {
        format(timeZone = timeZone, formatStyle = formatStyle)
    }
}