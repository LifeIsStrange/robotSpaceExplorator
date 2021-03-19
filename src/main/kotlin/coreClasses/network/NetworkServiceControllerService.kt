package coreClasses.network

import coreClasses.EmitterType
import coreClasses.Message
import coreClasses.MessageStageType
import coreClasses.NetworkChannel

class NetworkServiceControllerService(override var networkChannel: NetworkChannel) : NetworkService() {

    public override fun listenIncommingMessage(): Message? {
        var msg: Message? = networkChannel.messageQueue.firstOrNull()

        if (msg?.emitterType == EmitterType.Mission) {
            networkChannel.messageQueue.poll()
        }
        return msg
    }

    // sendMessageAuthorizationNextStage
    public override fun sendMessage(receivedMessageType: MessageStageType?, messageContent: String, newMessageType: MessageStageType) {
        this.setBestPossibleAvailableNetworkBandWidth()
        if (receivedMessageType != MessageStageType.Exploration) {
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
