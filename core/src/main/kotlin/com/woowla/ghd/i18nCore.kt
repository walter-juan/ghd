package com.woowla.ghd

internal object i18nCore {
    val generic_now =  "now"
    val generic_in_seconds: (Long) -> String = { "in $it seconds" }
    val generic_in_minutes: (Long) -> String = { "in $it minutes" }
    val generic_in_hours: (Long) -> String = { "in $it hours" }
    val generic_in_days: (Long) -> String = { "in $it days" }
    val generic_seconds_ago: (Long) -> String = { "$it seconds ago" }
    val generic_minutes_ago: (Long) -> String = { "$it minutes ago" }
    val generic_hours_ago: (Long) -> String = { "$it hours ago" }
    val generic_days_ago: (Long) -> String = { "$it days ago" }
    val generic_date_format: (String, String, String, String) -> String = { month, day, year, hour -> "$month $day, $year at $hour" }
}