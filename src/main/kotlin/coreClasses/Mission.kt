package coreClasses

import utils.Id
import utils.Utils

/**
 * this class is responsible for the scheduling of a Mission
 */
class Mission(val id: Id, var componentList: List<Component>, var network: Network) : Runnable {
    companion object {
        /** time is simulated by considering months as seconds  */
        var defaultMinStageTime = 1000f
        var defaultMaxStageTime = 5000f
    }
    // in seconds
    var startTime = Utils.getRandomNumberInRange(100f, 3000f)
    var missionId = Thread.currentThread().id
    //lateinit var myComp: Component
    // todo get random destination and network
    // in tons
    // todo fix fuel mismatch
    var fuel = Utils.getRandomNumberInRange(10f, 100000f)
    override fun run() {
        //this.myComp.showMessage()
        println(Thread.currentThread().name + " Mission starting.")
    }
    // todo allocate resources
    private fun scheduleStages() {
        this.boostStage()
        this.transitStage()
        this.landingStage()
        this.explorationStage()
    }

    fun boostStage() {
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


    init {
        println(Thread.currentThread().name + " Mission constructed.")
        scheduleStages()
    }
}