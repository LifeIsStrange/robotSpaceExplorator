package coreClasses.mission

import coreClasses.*
import coreClasses.network.Network
import utils.Id
import utils.Utils

/**
 * this class is responsible for the scheduling of a Mission
 */
// todo get random destination and network
// todo fix fuel mismatch
class Mission(val id: Id, var componentList: List<Component>, override var networkChannel: NetworkChannel) : Runnable, Network() {
    companion object {
        /** time is simulated by considering months as seconds  */
        var defaultMinStageTime = 1000f
        var defaultMaxStageTime = 5000f
    }
    var startTime = Utils.getRandomNumberInRange(100f, 3000f)
    var missionId = Thread.currentThread().id

    override fun run() {
        println(Thread.currentThread().name + " Mission starting.")
        println(Thread.currentThread().name + " Mission constructed.")
        println("---------")
        scheduleStages()
    }

    private fun scheduleStages() {
        this.boostStage()
        this.transitStage()
        this.landingStage()
        this.explorationStage()
    }

    private fun receiveStageAnswer(): Message? {
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
    private fun boostStage() {
        println(Thread.currentThread().name + " entering boost stage..")
        this.sendMessage(messageContent = "${Thread.currentThread().name} terminating boost stage", newMessageType = MessageType.Boost )
        //this.networkChannel.messageQueue.offer(Message(content = "${Thread.currentThread().name} terminating boost stage", EmitterType.Mission, MessageType.Boost))
        this.receiveStageAnswer()
    }
    // A variable burst of reports and
    // commands are sent at the transition between mission stages.
    // There are a variable number of types
    // of commands and reports for each mission.
    private fun transitStage() {
        println(Thread.currentThread().name + " entering interplanetary transit stage..")
        try {
            // todo failure rate 10% min
            Thread.sleep(
                Utils.getRandomNumberInRange(defaultMinStageTime, defaultMaxStageTime)
                    .toLong()
            )
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        this.sendMessage(messageContent = "${Thread.currentThread().name} terminating transit stage", newMessageType = MessageType.Transit )
        //this.networkChannel.messageQueue.offer(Message(content = "${Thread.currentThread().name} terminating transit stage", EmitterType.Mission, MessageType.Transit))
        this.receiveStageAnswer()
    }

    private fun landingStage() {
        println(Thread.currentThread().name + " entering landing stage..")
        this.sendMessage(messageContent = "${Thread.currentThread().name} terminating transit stage", newMessageType = MessageType.Landing )
        //this.networkChannel.messageQueue.offer(Message(content = "${Thread.currentThread().name} terminating transit stage", EmitterType.Mission, MessageType.Landing))
        this.receiveStageAnswer()
    }

    // Time can simulated by allowing a fixed ratio of wall clock time to mission time eg 1 sec : 1
    // month.
    // When waiting a mission 'sleeps'.
    private fun explorationStage() {
        println(Thread.currentThread().name + " entering exploration stage..")
        try {
            Thread.sleep(
                Utils.getRandomNumberInRange(defaultMinStageTime, defaultMaxStageTime)
                    .toLong()
            )
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        this.sendMessage(messageContent = "${Thread.currentThread().name} terminating transit stage", newMessageType = MessageType.Exploration )
        //this.networkChannel.messageQueue.offer(Message(content = "${Thread.currentThread().name} terminating transit stage", EmitterType.Mission, MessageType.Exploration))
    }

    override fun listenIncommingMessage(): Message? {
        return this.receiveStageAnswer()
    }

    override fun sendMessage(receivedMessageType: MessageType?, messageContent: String, newMessageType: MessageType) {
        this.networkChannel.messageQueue.offer(
            Message(
                content = messageContent,
                EmitterType.Mission,
                newMessageType
            )
        )
    }
}