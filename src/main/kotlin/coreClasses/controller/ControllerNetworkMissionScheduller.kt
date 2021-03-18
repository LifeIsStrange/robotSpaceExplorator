package coreClasses.controller

import coreClasses.Message
import coreClasses.Network

class ControllerNetworkMissionScheduller(var network: Network) : Runnable {
    private fun listenIncommingMessage() {
        var msg: Message? = network.messageQueue.poll()

        if (msg != null) {
            println(msg.content)
        }
    }
    override fun run() {
        while(true) {
            this.listenIncommingMessage()
        }
    }
}