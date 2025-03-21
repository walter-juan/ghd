package com.woowla.ghd.core.eventbus

import com.woowla.ghd.core.AppLogger
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@Deprecated("Future replacement with Flows")
class EventBus(
    private val appLogger: AppLogger,
) {
    private val eventBusDispatcher: CoroutineContext = Dispatchers.Default
    private val eventBusScope = CoroutineScope(eventBusDispatcher)

    private val events = MutableSharedFlow<Any>()
    private val subscriberJobs by lazy { mutableMapOf<Any, Set<Job>>() }

    fun <T: Any> publish(event: T) {
        eventBusScope.launch { events.emit(event) }
    }

    fun <T: Any> subscribe(subscriber: Any, scope: CoroutineScope, event: T, action: () -> Unit) {
        val subscriberJob = events
            .filter { it == event }
            .onEach { action.invoke() }
            .catch { ex -> appLogger.e("EventBus :: Exception, ${ex.message}", ex) }
            .launchIn(scope)
        subscriberJobs[subscriber] = getJobs(subscriber) + subscriberJob
    }

    fun unsubscribe(subscriber: Any) {
        getJobs(subscriber).forEach { job -> job.cancel() }
        subscriberJobs.remove(subscriber)
    }

    private fun getJobs(subscriber: Any): Set<Job> {
        return subscriberJobs[subscriber] ?: emptySet()
    }
}