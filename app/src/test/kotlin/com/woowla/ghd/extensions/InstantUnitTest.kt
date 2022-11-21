package com.woowla.ghd.extensions

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import java.time.Month
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant

class InstantUnitTest : StringSpec({
    val now = LocalDateTime(
        year = 2022,
        month = Month.APRIL,
        dayOfMonth = 12,
        hour = 15,
        minute = 25
    ).toInstant(TimeZone.UTC)

    "before should be true if other is after this" {
        val afterNow = now.plus(2.minutes)
        now.before(afterNow) shouldBe true
    }

    "after should be true if other is before this" {
        val beforeNow = now.minus(2.minutes)
        now.after(beforeNow) shouldBe true
    }
})