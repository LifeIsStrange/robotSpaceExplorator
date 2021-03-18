package coreClasses

import utils.Id
import utils.Range
import utils.Utils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.reflect.full.createInstance

/**
 * this class is responsible for the scheduling of the Missions
 */
/*
    You must implement the methods called by the
    mission controller to construct the mission vehicles from components, move missions along their
    stages, check for failures, send reports, send instructions and software updates. It is important to
    represent the communications networks (queues) too
 */
// The mission controller is a shared resource used for all missions.
object MissionScheduller {
    private var missionList :MutableList<Runnable> = mutableListOf();

    private fun getRandomizedComponentList(): List<Component> {
        val componentList = mutableListOf<Component>()

        for (componentType in Component::class.sealedSubclasses) {
            val numberOfOccurrencesOfThisComponent = Utils.getRandomNumberInRange(1f, 10f).toInt()
            for (occurrenceIdx in Range.range(numberOfOccurrencesOfThisComponent)) {
                componentList.add(componentType.createInstance())
            }
        }
        return componentList
    }

    private fun initMissions(numberOfSimultaneousMissions: Int,   createSharedNetwork: (missionId: Id) -> Network) {
        for (threadIdx in 0 until numberOfSimultaneousMissions) {
            val missionId = Utils.generateUUID();
            val mission =  Mission(missionId, getRandomizedComponentList(), createSharedNetwork(missionId))

            this.missionList.add(mission)
        }
    }

    private fun executeMissions(executor: ExecutorService) {
        this.missionList.forEach { executor.execute(it) }
    }
    fun scheduleMissions(numberOfSimultaneousMissions: Int,  createSharedNetwork: (missionId: Id) -> Network) {
        // might benefit from a ScheduledExecutorService or a forkJoinPool
        // note that we currently execute as much tasks as the threadPool size so there is no reuse ?
        this.initMissions(numberOfSimultaneousMissions, createSharedNetwork)
        val executor = Executors.newFixedThreadPool(numberOfSimultaneousMissions)
        this.executeMissions(executor)
        executor.shutdown()
        // busy wait ?
        while (!executor.isTerminated) {}
        println("All the missions have been completed!")
    }
}

class Controller() {
    private var networkMissionList = mutableListOf<Network>()
    private var numberOfSimultaneousMissions = 2

    init {
        MissionScheduller.scheduleMissions(this.numberOfSimultaneousMissions, this::createSharedNetwork)
    }

    private fun createSharedNetwork(missionId: Id): Network {
        var network = Network(missionId);

        this.networkMissionList.add(network)
        return network
    }
    //ChangeMissionStage
    //Verify failure
    //Send Instruction
    //Software update

}