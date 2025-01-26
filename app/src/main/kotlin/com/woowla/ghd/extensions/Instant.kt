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
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

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
 * Transform a [Instant] to a human-readable relative screen or date text if maximums are reached.
 * Examples:
 *  - in 2 minutes
 *  - in 1 hour
 *  - 2 hours ago
 *  - 1 day ago
 *  - 15 Dec 2024, 18:22:18
 */
fun Instant.toRelativeString(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    maximumSeconds: Duration = 60.seconds,
    maximumMinutes: Duration = 60.minutes,
    maximumHours: Duration = 24.hours,
    maximumDays: Duration = 400.days,
): String {
    val now = Clock.System.now()
    val duration = now - this
    val isFuture = duration.isNegative()
    val durationAbs = duration.absoluteValue

    return when {
        durationAbs <= 0.seconds -> {
            i18n.generic_now
        }

        durationAbs < maximumSeconds -> {
            val seconds = durationAbs.inWholeSeconds
            if (isFuture) { i18n.generic_in_seconds(seconds) } else { i18n.generic_seconds_ago(seconds) }
        }

        durationAbs < maximumMinutes -> {
            val minutes = durationAbs.inWholeMinutes
            if (isFuture) { i18n.generic_in_minutes(minutes) } else { i18n.generic_minutes_ago(minutes) }
        }

        durationAbs < maximumHours -> {
            val hours = durationAbs.inWholeHours
            if (isFuture) { i18n.generic_in_hours(hours) } else { i18n.generic_hours_ago(hours) }
        }

        durationAbs < maximumDays -> {
            val days = durationAbs.inWholeDays
            if (isFuture) { i18n.generic_in_days(days) } else { i18n.generic_days_ago(days) }
        }

        else -> {
            val month = format(timeZone, DateTimeFormatter.ofPattern("MMMM"))
            val day = format(timeZone, DateTimeFormatter.ofPattern("dd"))
            val year = format(timeZone, DateTimeFormatter.ofPattern("yyyy"))
            val hour = format(timeZone, DateTimeFormatter.ofPattern("HH:mm"))
            i18n.generic_date_format(month, day, year, hour)
        }
    }
}