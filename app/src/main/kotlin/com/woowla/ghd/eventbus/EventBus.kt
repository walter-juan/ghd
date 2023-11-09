package com.woowla.ghd.eventbus

import com.woowla.ghd.AppLogger
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

object EventBus {
    private val eventBusDispatcher: CoroutineContext = Dispatchers.Default
    private val eventBusScope = CoroutineScope(eventBusDispatcher)

    private val events = MutableSharedFlow<Event>()
    private val subscriberJobs by lazy { mutableMapOf<Any, Set<Job>>() }

    fun publish(event: Event) {
        eventBusScope.launch { events.emit(event) }
    }

    fun subscribe(subscriber: Any, scope: CoroutineScope, event: Event, action: () -> Unit) {
        val subscriberJob = events
            .filter { it == event }
            .onEach { action.invoke() }
            .catch { ex -> AppLogger.e("EventBus :: Exception, ${ex.message}", ex) }
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