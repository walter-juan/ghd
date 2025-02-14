package com.woowla.ghd

import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt
import kotlin.random.nextLong
import kotlinx.datetime.Instant

object RandomValues {
    val alphabet: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')

    fun randomBoolean(): Boolean = Random.nextBoolean()

    fun randomLong(range: LongRange = 0L..999L): Long = Random.nextLong(range)
    fun randomInt(range: IntRange = 0..999): Int = Random.nextInt(range)

    fun randomString(size: Int = 2): String = List(size) { alphabet.random() }.joinToString(separator = "")

    fun randomUUID(): UUID = UUID.randomUUID()
    fun randomId(): String = randomUUID().toString()

    fun randomUrl(): String = "https://${randomString(randomInt(3..15))}.com"

    fun randomInstant(): Instant {
        val minEpochSeconds = Instant.DISTANT_PAST.epochSeconds
        val maxEpochSeconds = Instant.DISTANT_FUTURE.epochSeconds

        val randomEpochSeconds = Random.nextLong(minEpochSeconds, maxEpochSeconds + 1)
        return Instant.fromEpochSeconds(randomEpochSeconds)
    }
}