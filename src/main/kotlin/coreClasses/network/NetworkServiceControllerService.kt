package coreClasses.network

import utils.ANSI_RED
import utils.ANSI_RESET
import utils.Utils

class NetworkServiceControllerService(override var networkChannel: NetworkChannel) : NetworkService() {

    public override fun listenIncommingMessage(): Message? {
        var msg: Message? = networkChannel.messageQueue.firstOrNull()

        if (msg?.emitterType == EmitterType.Mission) {
            networkChannel.messageQueue.poll()
        }
        return msg
    }

    public override suspend fun sendMessage(
        receivedMessageType: MessageType?,
        messageContent: String,
        newMessageType: MessageType,
        distanceFromEarthQuintile: Int
    ) {
        this.setBestPossibleAvailableNetworkBandWidth()
        if (receivedMessageType != MessageType.ExplorationStage) {
            val sizeMessage = this.getPayloadSizeForMessageType(newMessageType)

            //FIXME : remove comments
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
