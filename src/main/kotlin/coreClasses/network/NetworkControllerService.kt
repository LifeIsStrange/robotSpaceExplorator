package coreClasses.network

import coreClasses.EmitterType
import coreClasses.Message
import coreClasses.MessageType
import coreClasses.NetworkChannel

class NetworkControllerService(override var networkChannel: NetworkChannel) : Network{
    public override fun listenIncommingMessage(): Message? {
        var msg: Message? = networkChannel.messageQueue.firstOrNull()

        if (msg?.emitterType == EmitterType.Mission) {
            networkChannel.messageQueue.poll()
        }
        return msg;
    }

    //sendMessageAuthorizationNextStage
    public override fun sendMessage(receivedMessageType: MessageType?, messageContent: String, newMessageType: MessageType) {
        if (receivedMessageType != MessageType.Exploration) {
            this.networkChannel.messageQueue.add(
                Message(
                    messageContent,
                    emitterType = EmitterType.Controller,
                    messageType = newMessageType
                )
            )
        }
    }
}