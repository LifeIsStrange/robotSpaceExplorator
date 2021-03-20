package coreClasses.network

class NetworkControllerService(override var networkChannel: NetworkChannel) : NetworkService() {

    override fun listenIncommingMessage(): Message? {
        val msg: Message? = networkChannel.messageQueue.firstOrNull()

        if (msg?.emitterType == EmitterType.Mission) {
            networkChannel.messageQueue.poll()
        }
        return msg
    }

    override suspend fun sendMessage(
        receivedMessageType: MessageType?,
        messageContent: String,
        newMessageType: MessageType,
        distanceFromEarthQuintile: Int
    ) {
        this.setBestPossibleAvailableNetworkBandWidth()
        if (receivedMessageType != MessageType.ExplorationStage) {
            val sizeMessage = this.getPayloadSizeForMessageType(newMessageType)

            this.simulateTimeToTransferMessage(sizeMessage, distanceFromEarthQuintile)
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
