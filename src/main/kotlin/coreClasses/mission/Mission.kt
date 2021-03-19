package coreClasses.mission

import coreClasses.*
import coreClasses.network.NetworkServiceMissionService
import utils.Id
import utils.Utils

/**
 * this class is responsible for the scheduling of a Mission
 */
// todo get random destination and network
// todo fix fuel mismatch
class Destination(val name: String, val distance: Float)

class Mission(
      val id: Id,
      var componentList: List<Component>,
      private var networkChannel: NetworkChannel,
      private var destination: Destination
    ) : Runnable {
    private var missionNetworkService = NetworkServiceMissionService(networkChannel)

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

    private fun boostStage() {
        println(Thread.currentThread().name + " entering boost stage..")
        this.missionNetworkService.sendMessage(messageContent = "${Thread.currentThread().name} terminating boost stage", newMessageType = MessageStageType.Boost)
        // this.networkChannel.messageQueue.offer(Message(content = "${Thread.currentThread().name} terminating boost stage", EmitterType.Mission, MessageType.Boost))
        this.missionNetworkService.receiveStageAnswer()
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
        this.missionNetworkService.sendMessage(messageContent = "${Thread.currentThread().name} terminating transit stage", newMessageType = MessageStageType.Transit)
        // this.networkChannel.messageQueue.offer(Message(content = "${Thread.currentThread().name} terminating transit stage", EmitterType.Mission, MessageType.Transit))
        this.missionNetworkService.receiveStageAnswer()
    }

    private fun landingStage() {
        println(Thread.currentThread().name + " entering landing stage..")
        this.missionNetworkService.sendMessage(messageContent = "${Thread.currentThread().name} terminating landing stage", newMessageType = MessageStageType.Landing)
        // this.networkChannel.messageQueue.offer(Message(content = "${Thread.currentThread().name} terminating transit stage", EmitterType.Mission, MessageType.Landing))
        this.missionNetworkService.receiveStageAnswer()
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
        this.missionNetworkService.sendMessage(messageContent = "${Thread.currentThread().name} terminating exploration stage", newMessageType = MessageStageType.Exploration)
        // this.networkChannel.messageQueue.offer(Message(content = "${Thread.currentThread().name} terminating transit stage", EmitterType.Mission, MessageType.Exploration))
    }
}
