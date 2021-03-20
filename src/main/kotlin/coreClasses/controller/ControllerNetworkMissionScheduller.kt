package coreClasses.controller

import coreClasses.network.EmitterType
import coreClasses.network.MessageType
import coreClasses.network.NetworkChannel
import coreClasses.network.NetworkControllerService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import utils.*
import java.util.concurrent.ExecutorService

class ControllerNetworkMissionScheduller(
    private var networkChannel: NetworkChannel,
    private var missionsExecutor: ExecutorService
) : Runnable {

    val controllerNetworkService = NetworkControllerService(networkChannel)

    override fun run() {
        while (!this.missionsExecutor.isTerminated) {
            val msg = this.controllerNetworkService.listenIncommingMessage()

            if (msg?.emitterType == EmitterType.Mission) {
                when {
                    msg.messageType.name.endsWith("Stage") && msg.messageType != MessageType.InterTransitStage -> {
                        Utils.log("\n$ANSI_CYAN ${msg.content} $ANSI_RESET\n")

                        GlobalScope.launch {
                            controllerNetworkService.sendMessage(
                                receivedMessageType = msg.messageType,
                                messageContent = "end of stage: \"${msg.messageType}\" accepted, you can go on ${msg.messageType.next()}",
                                newMessageType = msg.messageType.next()
                            )
                        }
                    }
                    msg.messageType == MessageType.Failure -> {
                        Utils.log("\n$ANSI_PURPLE ${msg.content} $ANSI_RESET\n")

                        GlobalScope.launch {
                            controllerNetworkService.sendMessage(
                                messageContent = "send the code to fix the bug caused by the failure in mission \"${networkChannel.missionId}\"",
                                newMessageType = MessageType.SoftwareUpdate
                            )
                        }
                    }
                    msg.messageType == MessageType.AbortMission -> {
                        Utils.log("\n$ANSI_RED ${msg.content} $ANSI_RESET\n")
                    }
                    msg.messageType == MessageType.InterTransitStage -> {
                        Utils.log("\n ${msg.content} \n")
                    }
                    else -> Utils.log(msg.content) // components messages
                }
            }
        }
    }
}
