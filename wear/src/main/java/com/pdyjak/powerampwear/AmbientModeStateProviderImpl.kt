package com.pdyjak.powerampwear

import com.pdyjak.powerampwear.common.SimpleEvent
import com.pdyjak.powerampwear.player.AmbientModeStateProvider

class AmbientModeStateProviderImpl : AmbientModeStateProvider {
    override val onAmbientModeChanged = SimpleEvent()

    override var isInAmbientMode: Boolean = false
        get
        internal set(value) {
            if (field == value) return
            field = value;
            onAmbientModeChanged()
        }
}