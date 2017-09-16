package com.pdyjak.powerampwear

import com.pdyjak.powerampwear.player.AmbientModeStateProvider

class AmbientModeStateProviderImpl : AmbientModeStateProvider {

    private val mAmbientModeListeners = HashSet<AmbientModeStateProvider.Listener>()

    override var isInAmbientMode: Boolean = false
        get
        internal set(value) {
            if (field == value) return
            field = value;
            val copy = HashSet(mAmbientModeListeners)
            for (listener in copy) listener.onAmbientModeStateChanged()
        }

    override fun addAmbientModeListener(listener: AmbientModeStateProvider.Listener) {
        mAmbientModeListeners.add(listener)
    }

    override fun removeAmbientModeListener(listener: AmbientModeStateProvider.Listener) {
        mAmbientModeListeners.remove(listener)
    }
}