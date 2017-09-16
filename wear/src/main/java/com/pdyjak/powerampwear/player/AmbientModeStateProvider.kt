package com.pdyjak.powerampwear.player

interface AmbientModeStateProvider {

    interface Listener {
        fun onAmbientModeStateChanged()
    }

    val isInAmbientMode: Boolean
    fun addAmbientModeListener(listener: Listener)
    fun removeAmbientModeListener(listener: Listener)
}
