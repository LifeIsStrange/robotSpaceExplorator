package coreClasses.controller

import coreClasses.EmitterType
import coreClasses.Message
import coreClasses.MessageType
import coreClasses.NetworkChannel
import coreClasses.network.Network
import utils.next
import java.util.concurrent.ExecutorService

class ControllerNetworkMissionScheduller(
    override var networkChannel: NetworkChannel,
    private var missionsExecutor: ExecutorService) : Runnable, Network() {

    override fun listenIncommingMessage(): Message? {
        var msg: Message? = networkChannel.messageQueue.firstOrNull()

        if (msg?.emitterType == EmitterType.Mission) {
            networkChannel.messageQueue.poll()
        }
        return msg;
    }

    //sendMessageAuthorizationNextStage
    override fun sendMessage(receivedMessageType: MessageType?, messageContent: String, newMessageType: MessageType) {
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

    override fun run() {
        while(!this.missionsExecutor.isTerminated) {
            val msg = this.listenIncommingMessage()

            if (msg?.emitterType == EmitterType.Mission) {
                println(msg.content)
                this.sendMessage(
                    receivedMessageType = msg.messageType,
                    messageContent = "end of stage: ${networkChannel.missionId} ${msg.messageType} accepted, you can go on ${msg.messageType.next()}",
                    newMessageType = msg.messageType.next()
                )
            }
        }
    }
}