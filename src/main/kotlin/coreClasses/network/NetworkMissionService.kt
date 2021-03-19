package coreClasses.network

import coreClasses.EmitterType
import coreClasses.Message
import coreClasses.MessageType
import coreClasses.NetworkChannel

class NetworkMissionService(override var networkChannel: NetworkChannel) : Network {
    fun receiveStageAnswer(): Message? {
        var msg: Message? = null;
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
        this.networkChannel.messageQueue.offer(
            Message(
                content = messageContent,
                EmitterType.Mission,
                newMessageType
            )
        )
    }
}