package com.pdyjak.powerampwear.common

import java.util.*
import kotlin.collections.HashSet

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

    operator fun invoke() {
        val copy = HashSet(mEventHandlers)
        for (handler in copy) handler.invoke()
        if (mWeakEvent != null) mWeakEvent!!.invoke()
    }

    fun weakly(): SimpleEvent {
        if (mWeak) throw IllegalStateException("Already weak!")
        if (mWeakEvent != null) return mWeakEvent!!
        mWeakEvent = SimpleEvent(Collections.newSetFromMap(
                WeakHashMap<() -> Unit, Boolean>()), true)
        return mWeakEvent!!
    }
}

class Event<ArgsType> {
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

    operator fun invoke(args: ArgsType) {
        val copy = HashSet(mEventHandlers)
        for (handler in copy) handler.invoke(args)
        if (mWeakEvent != null) mWeakEvent!!.invoke(args)
    }

    fun weakly(): Event<ArgsType> {
        if (mWeak) throw IllegalStateException("Already weak!")
        if (mWeakEvent != null) return mWeakEvent!!
        mWeakEvent = Event(Collections.newSetFromMap(
                WeakHashMap<(ArgsType) -> Unit, Boolean>()), true)
        return mWeakEvent!!
    }
}