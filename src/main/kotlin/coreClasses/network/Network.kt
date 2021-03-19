package coreClasses.network

import coreClasses.Message
import coreClasses.MessageType
import coreClasses.NetworkChannel

abstract class Network {
    protected abstract var networkChannel: NetworkChannel

    protected abstract fun listenIncommingMessage(): Message?
    protected abstract fun sendMessage(receivedMessageType: MessageType? = null, messageContent: String, newMessageType: MessageType)
}
