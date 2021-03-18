package coreClasses.controller

import coreClasses.Message
import coreClasses.Network

class NetworkScheduller(var network: Network) : Runnable {
    private fun listenIncommingMessage() {
        var msg: Message?;

        while(run {
                msg = network.messageQueue.poll()
                msg
            } != null) {
            //for (msg = mission.messageQueue.poll(); msg != null)
            println("boucle" + network.missionId)
            println(msg?.content)
        }
    }
    override fun run() {
        this.listenIncommingMessage()
    }
}