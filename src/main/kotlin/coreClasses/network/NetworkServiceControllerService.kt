package coreClasses.network

class NetworkServiceControllerService(override var networkChannel: NetworkChannel) : NetworkService() {

    public override fun listenIncommingMessage(): Message? {
        var msg: Message? = networkChannel.messageQueue.firstOrNull()

        if (msg?.emitterType == EmitterType.Mission) {
            //TODO log in file
            println(msg.content)
            networkChannel.messageQueue.poll()
        }
        return msg
    }

    // sendMessageAuthorizationNextStage
    public override fun sendMessage(receivedMessageType: MessageType?, messageContent: String, newMessageType: MessageType) {
        this.setBestPossibleAvailableNetworkBandWidth()
        if (receivedMessageType != MessageType.ExplorationStage) {
            val sizeMessage = this.getPayloadSizeForMessageType(newMessageType)
            this.networkChannel.messageQueue.add(
                Message(
                    this.addHeaderToMessageContent(messageContent, sizeMessage, networkChannel.missionId, newMessageType),
                    emitterType = EmitterType.Controller,
                    messageType = newMessageType,
                    sizeMessage
                )
            )
        }
    }
}
