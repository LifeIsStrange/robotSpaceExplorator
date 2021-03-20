package coreClasses.network

import utils.ANSI_PURPLE
import utils.ANSI_RESET
import utils.Utils

//TODO
class NetworkServiceMissionService(override var networkChannel: NetworkChannel) : NetworkService() {
    fun receiveStageAnswer(): Message? {
        var msg: Message? = null

        while (true) {
            msg = this.networkChannel.messageQueue.firstOrNull()

            if (msg?.emitterType == EmitterType.Controller) {

                if (msg.messageType == MessageType.SoftwareUpdate) {
                    Utils.log("\n$ANSI_PURPLE ${msg.content} $ANSI_RESET \n")
                } else {
                    Utils.log(msg.content)
                }
                //println(msg.content)
                this.networkChannel.messageQueue.poll()
                return msg
            }
        }
        return msg
    }

    override fun listenIncommingMessage(): Message? {
        return this.receiveStageAnswer()
    }

    public override suspend fun sendMessage(
        receivedMessageType: MessageType?,
        messageContent: String,
        newMessageType: MessageType,
        distanceFromEarthQuintile: Int
    ) {
        this.setBestPossibleAvailableNetworkBandWidth()
        val sizeMessage = this.getPayloadSizeForMessageType(newMessageType)

        //FIXME : remove comments

        this.simulateTimeToTransferMessage(sizeMessage, distanceFromEarthQuintile)
        this.networkChannel.messageQueue.offer(
            Message(
                this.addHeaderToMessageContent(messageContent, sizeMessage, networkChannel.missionId, newMessageType),
                EmitterType.Mission,
                newMessageType,
                sizeMessage
            )
        )
    }
}
