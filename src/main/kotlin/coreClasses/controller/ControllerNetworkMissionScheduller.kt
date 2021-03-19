package coreClasses.controller

import coreClasses.EmitterType
import coreClasses.NetworkChannel
import coreClasses.network.NetworkServiceControllerService
import utils.next
import java.util.concurrent.ExecutorService

class ControllerNetworkMissionScheduller(
    private var networkChannel: NetworkChannel,
    private var missionsExecutor: ExecutorService
) : Runnable {

    val controllerNetworkService = NetworkServiceControllerService(networkChannel)

    override fun run() {
        while (!this.missionsExecutor.isTerminated) {
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
