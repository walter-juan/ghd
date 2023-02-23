package com.woowla.ghd.extensions

import com.woowla.ghd.presentation.app.i18n
import java.time.format.FormatStyle
import kotlin.time.Duration
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import java.time.format.DateTimeFormatter

fun Instant.after(other: Instant) = compareTo(other) > 0

fun Instant.before(other: Instant) = compareTo(other) < 0

/**
 * Format instant
 */
fun Instant.format(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    formatter: DateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM),
): String {
    return formatter.format(toLocalDateTime(timeZone).toJavaLocalDateTime())
}

/**
 * Transform a [Instant] to a human-readable "minutes/hours ago" or date text.
 * This will return the date text formatted if the duration is greater than [maxHours]
 * @param maxHours The maximum hours to be considered as human-readable
 */
fun Instant.toHRString(
    maxHours: Long = 24,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String {
    val duration: Duration = Clock.System.now() - this

    val minutes = duration.inWholeMinutes
    val hours = duration.inWholeHours

    return if (minutes <= 0) {
        i18n.generic_now
    } else if (minutes < 60) {
        i18n.generic_minutes_ago(minutes)
    } else if (hours < maxHours) {
        i18n.generic_hours_ago(hours)
    } else {
        val month = format(timeZone, DateTimeFormatter.ofPattern("MMMM"))
        val day = format(timeZone, DateTimeFormatter.ofPattern("dd"))
        val year = format(timeZone, DateTimeFormatter.ofPattern("yyyy"))
        val hour = format(timeZone, DateTimeFormatter.ofPattern("HH:mm"))
        i18n.generic_date_format(month, day, year, hour)
    }
}

/**
 * Transform a [Instant] to a human-readable "minutes/hours ago" or date text.
 */
fun Instant.toAgoString(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): String {
    val now = Clock.System.now()
    val diff = now - this
    val minutes = diff.inWholeMinutes
    val hours = diff.inWholeHours
    val days = diff.inWholeDays

    return if (minutes <= 0) {
        i18n.generic_now
    } else if (hours <= 0) {
        i18n.generic_minutes_ago(minutes)
    } else if (days <= 0) {
        i18n.generic_hours_ago(hours)
    } else if (days <= 20) {
        i18n.generic_days_ago(days)
    } else {
        val month = format(timeZone, DateTimeFormatter.ofPattern("MMMM"))
        val day = format(timeZone, DateTimeFormatter.ofPattern("dd"))
        val year = format(timeZone, DateTimeFormatter.ofPattern("yyyy"))
        val hour = format(timeZone, DateTimeFormatter.ofPattern("HH:mm"))
        i18n.generic_date_format(month, day, year, hour)
    }
}