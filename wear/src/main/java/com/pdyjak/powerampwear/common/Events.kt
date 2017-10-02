package com.pdyjak.powerampwear.common

import java.util.*
import kotlin.collections.HashSet

abstract class EventArgs

// TODO: remove duplication

class SimpleEvent {
    private val mEventHandlers: MutableSet<() -> Unit>
    private val mWeak: Boolean
    private var mWeakEvent: SimpleEvent? = null

    constructor() : this(HashSet(), false)

    private constructor(handlersContainer: MutableSet<() -> Unit>, weak: Boolean) {
        mEventHandlers = handlersContainer
        mWeak = weak
    }

    operator fun plusAssign(eventHandler: () -> Unit) {
        mEventHandlers.add(eventHandler)
    }

    operator fun minusAssign(eventHandler: () -> Unit) {
        mEventHandlers.remove(eventHandler)
    }

    fun notifyEventChanged() {
        val copy = HashSet(mEventHandlers)
        for (handler in copy) handler.invoke()
        if (mWeakEvent != null) mWeakEvent!!.notifyEventChanged()
    }

    fun weakly(): SimpleEvent {
        if (mWeak) throw IllegalStateException("Already weak!")
        if (mWeakEvent != null) return mWeakEvent!!
        mWeakEvent = SimpleEvent(Collections.newSetFromMap(
                WeakHashMap<() -> Unit, Boolean>()), true)
        return mWeakEvent!!
    }
}

class Event<ArgsType : EventArgs?> {
    private val mEventHandlers: MutableSet<(ArgsType) -> Unit>
    private val mWeak: Boolean
    private var mWeakEvent: Event<ArgsType>? = null

    constructor() : this(HashSet(), false)

    private constructor(handlersContainer: MutableSet<(ArgsType) -> Unit>, weak: Boolean) {
        mEventHandlers = handlersContainer
        mWeak = weak
    }

    operator fun plusAssign(eventHandler: (ArgsType) -> Unit) {
        mEventHandlers.add(eventHandler)
    }

    operator fun minusAssign(eventHandler: (ArgsType) -> Unit) {
        mEventHandlers.remove(eventHandler)
    }

    fun notifyEventChanged(args: ArgsType) {
        val copy = HashSet(mEventHandlers)
        for (handler in copy) handler.invoke(args)
        if (mWeakEvent != null) mWeakEvent!!.notifyEventChanged(args)
    }

    fun weakly(): Event<ArgsType> {
        if (mWeak) throw IllegalStateException("Already weak!")
        if (mWeakEvent != null) return mWeakEvent!!
        mWeakEvent = Event(Collections.newSetFromMap(
                WeakHashMap<(ArgsType) -> Unit, Boolean>()), true)
        return mWeakEvent!!
    }
}