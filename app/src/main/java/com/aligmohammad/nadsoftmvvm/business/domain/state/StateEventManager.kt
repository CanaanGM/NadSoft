package com.aligmohammad.nadsoftmvvm.business.domain.state

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.aligmohammad.nadsoftmvvm.util.printLogD


class StateEventManager {

    private val activeStateEvents: HashMap<String, StateEvent> = HashMap()

    private val _shouldDisplayProgressBar: MutableLiveData<Boolean> = MutableLiveData()

    val shouldDisplayProgressBar: LiveData<Boolean>
        get() = _shouldDisplayProgressBar

    fun getActiveJobNames(): MutableSet<String> {
        return activeStateEvents.keys
    }

    fun clearActiveStateEventCounter() {
        printLogD("DCM", "Clear active state events")
        activeStateEvents.clear()
        syncNumActiveStateEvents()
    }

    fun addStateEvent(stateEvent: StateEvent) {
        activeStateEvents.put(stateEvent.eventName(), stateEvent)
        syncNumActiveStateEvents()
    }

    fun removeStateEvent(stateEvent: StateEvent?) {
        printLogD("DCM sem", "remove state event: ${stateEvent?.eventName()}")
        activeStateEvents.remove(stateEvent?.eventName())
        syncNumActiveStateEvents()
    }

    fun isStateEventActive(stateEvent: StateEvent): Boolean {
        printLogD(
            "DCM sem", "is state event active? " +
                    "${activeStateEvents.containsKey(stateEvent.eventName())}"
        )
        return activeStateEvents.containsKey(stateEvent.eventName())
    }

    private fun syncNumActiveStateEvents() {
        var shouldDisplayProgressBar = false
        for (stateEvent in activeStateEvents.values) {
            if (stateEvent.shouldDisplayProgressBar()) {
                shouldDisplayProgressBar = true
            }
        }
        _shouldDisplayProgressBar.value = shouldDisplayProgressBar
    }
}

















