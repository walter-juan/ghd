package com.woowla.ghd.extensions

import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Coroutine-based solution for delayed and periodic work. May fire once (if [interval] omitted)
 * or periodically ([startDelay] defaults to [interval] in this case), replacing both
 * `Observable.timer()` & `Observable.interval()` from RxJava.
 *
 * In contrast to RxJava, intervals are calculated since previous run completion; this is more
 * convenient for potentially long work (prevents overlapping) and does not suffer from queueing
 * multiple invocations in Doze mode on Android.
 *
 * Dispatcher is inherited from scope, may be overridden via [context] parameter.
 *
 * Inspired by [https://github.com/Kotlin/kotlinx.coroutines/issues/1186#issue-443483801]
 * Source from: [https://gist.github.com/gmk57/67591e0c878cedc2a318c10b9d9f4c0c]
 */
fun CoroutineScope.timer(
    interval: Duration = Duration.ZERO,
    startDelay: Duration = interval,
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend () -> Unit
): Job = launch(context) {
    delay(startDelay)
    do {
        block()
        delay(interval)
    } while (interval > Duration.ZERO)
}