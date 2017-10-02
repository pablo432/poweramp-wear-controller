package com.pdyjak.powerampwear.player

import com.pdyjak.powerampwear.common.SimpleEvent

interface AmbientModeStateProvider {
    val isInAmbientMode: Boolean
    val onAmbientModeChanged: SimpleEvent
}
