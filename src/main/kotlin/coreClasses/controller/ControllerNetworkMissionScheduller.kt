package coreClasses.controller

import coreClasses.network.EmitterType
import coreClasses.network.NetworkChannel
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
                if (msg.messageType.name.endsWith("Stage") ) {
                    this.controllerNetworkService.sendMessage(
                        receivedMessageType = msg.messageType,
                        messageContent = "end of stage: \"${msg.messageType}\" accepted, you can go on ${msg.messageType.next()}",
                        newMessageType = msg.messageType.next()
                    )
                }
            }
        }
    }
}
