package coreClasses.controller

import coreClasses.EmitterType
import coreClasses.Message
import coreClasses.MessageType
import coreClasses.NetworkChannel
import coreClasses.network.Network
import coreClasses.network.NetworkControllerService
import utils.next
import java.util.concurrent.ExecutorService

class ControllerNetworkMissionScheduller(
    private var networkChannel: NetworkChannel,
    private var missionsExecutor: ExecutorService): Runnable {

    val controllerNetworkService = NetworkControllerService(networkChannel)

    override fun run() {
        while(!this.missionsExecutor.isTerminated) {
            val msg = this.controllerNetworkService.listenIncommingMessage()

            if (msg?.emitterType == EmitterType.Mission) {
                println(msg.content)
                this.controllerNetworkService.sendMessage(
                    receivedMessageType = msg.messageType,
                    messageContent = "end of stage: ${networkChannel.missionId} ${msg.messageType} accepted, you can go on ${msg.messageType.next()}",
                    newMessageType = msg.messageType.next()
                )
            }
        }
    }
}