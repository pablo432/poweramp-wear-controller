package com.pdyjak.powerampwear

import com.google.android.gms.wearable.MessageEvent

interface MessageListener {
    fun onMessageReceived(messageEvent: MessageEvent)
}
