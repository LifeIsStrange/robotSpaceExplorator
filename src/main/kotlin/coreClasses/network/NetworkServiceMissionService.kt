package coreClasses.network

class NetworkServiceMissionService(override var networkChannel: NetworkChannel) : NetworkService() {
    fun receiveStageAnswer(): Message? {
        var msg: Message? = null

        while (true) {
            msg = this.networkChannel.messageQueue.firstOrNull()

            if (msg?.emitterType == EmitterType.Controller) {
                println(msg.content)
                this.networkChannel.messageQueue.poll()
                return msg
            }
        }
        return msg
    }

    override fun listenIncommingMessage(): Message? {
        return this.receiveStageAnswer()
    }

    public override fun sendMessage(receivedMessageType: MessageType?, messageContent: String, newMessageType: MessageType) {
        this.setBestPossibleAvailableNetworkBandWidth()
        val sizeMessage = this.getPayloadSizeForMessageType(newMessageType)
        this.networkChannel.messageQueue.offer(
            Message(
                this.addHeaderToMessageContent(messageContent, sizeMessage, networkChannel.missionId),
                EmitterType.Mission,
                newMessageType,
                sizeMessage
            )
        )
    }
}
