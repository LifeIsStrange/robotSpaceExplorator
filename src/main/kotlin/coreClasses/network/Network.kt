package coreClasses.network

import coreClasses.Message
import coreClasses.MessageType
import coreClasses.NetworkChannel

interface Network {
    abstract var networkChannel: NetworkChannel

    abstract fun listenIncommingMessage(): Message?
    abstract fun sendMessage(receivedMessageType: MessageType? = null, messageContent: String, newMessageType: MessageType)
}
