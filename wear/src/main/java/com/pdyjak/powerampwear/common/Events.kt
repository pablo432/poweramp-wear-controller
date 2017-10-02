package com.pdyjak.powerampwear.common

abstract class EventArgs

class SimpleEvent {
    private val mEventHandlers: HashSet<() -> Unit> = HashSet()

    operator fun plusAssign(eventHandler: () -> Unit) {
        mEventHandlers.add(eventHandler)
    }

    operator fun minusAssign(eventHandler: () -> Unit) {
        mEventHandlers.remove(eventHandler)
    }

    fun notifyEventChanged() {
        val copy = HashSet(mEventHandlers)
        for (handler in copy) handler.invoke()
    }
}

class Event<ArgsType : EventArgs?> {
    private val mEventHandlers: HashSet<(ArgsType) -> Unit> = HashSet()

    operator fun plusAssign(eventHandler: (ArgsType) -> Unit) {
        mEventHandlers.add(eventHandler)
    }

    operator fun minusAssign(eventHandler: (ArgsType) -> Unit) {
        mEventHandlers.remove(eventHandler)
    }

    fun notifyEventChanged(args: ArgsType) {
        val copy = HashSet(mEventHandlers)
        for (handler in copy) handler.invoke(args)
    }
}