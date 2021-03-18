package coreClasses

import utils.Id
import utils.Utils

/**
 * this class is responsible for the scheduling of a Mission
 */
// todo get random destination and network
// todo fix fuel mismatch
class Mission(val id: Id, var componentList: List<Component>, var network: Network) : Runnable {
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
        scheduleStages()
    }

    private fun scheduleStages() {
        this.boostStage()
        this.network.messageQueue.offer(Message(content = "terminating boost stage"))
        this.transitStage()
        this.network.messageQueue.offer(Message(content = "terminating transit stage"))
        this.landingStage()
        this.explorationStage()
    }

    private fun boostStage() {
        println("entering boost stage..")
    }
    // A variable burst of reports and
    // commands are sent at the transition between mission stages.
    // There are a variable number of types
    // of commands and reports for each mission.
    private fun transitStage() {
        println("entering interplanetary transit stage..")
        try {
            // todo failure rate 10% min
            Thread.sleep(
                Utils.getRandomNumberInRange(defaultMinStageTime, defaultMaxStageTime)
                    .toLong()
            )
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    private fun landingStage() {
        println("entering landing stage..")
    }

    // Time can simulated by allowing a fixed ratio of wall clock time to mission time eg 1 sec : 1
    // month.
    // When waiting a mission 'sleeps'.
    private fun explorationStage() {
        println("entering exploration stage..")
        try {
            Thread.sleep(
                Utils.getRandomNumberInRange(defaultMinStageTime, defaultMaxStageTime)
                    .toLong()
            )
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }
}