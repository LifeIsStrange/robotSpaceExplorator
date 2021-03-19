package coreClasses.controller

import coreClasses.EmitterType
import coreClasses.Message
import coreClasses.MessageType
import coreClasses.Network
import utils.next
import java.util.concurrent.ExecutorService

class ControllerNetworkMissionScheduller(private var network: Network, private var missionsExecutor: ExecutorService) : Runnable {
    private fun listenIncommingMessage() {
        var msg: Message? = network.messageQueue.firstOrNull()

        if (msg?.emitterType == EmitterType.Mission) {
            network.messageQueue.poll()
        }
        if (msg != null) {
            if (msg.emitterType == EmitterType.Mission) {
                println(msg.content)
                sendMessageAuthorizationNextStage(msg.messageType)
            }
        }
    }

    private fun sendMessageAuthorizationNextStage(messageType: MessageType) {
        if (messageType != MessageType.Exploration) {
            this.network.messageQueue.add(
                Message(
                    content = "end of stage: ${network.missionId} $messageType accepted, you can go on ${messageType.next()}",
                    emitterType = EmitterType.Controller,
                    messageType = messageType.next()
                )
            )
        }
    }

    override fun run() {
        while(!this.missionsExecutor.isTerminated) {
            this.listenIncommingMessage()
        }
    }
}