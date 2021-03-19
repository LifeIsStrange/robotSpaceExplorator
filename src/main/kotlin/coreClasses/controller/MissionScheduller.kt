package coreClasses.controller

import coreClasses.Component
import coreClasses.mission.Mission
import coreClasses.NetworkChannel
import utils.Id
import utils.Range
import utils.Utils
import java.util.concurrent.ExecutorService
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
class MissionScheduller(private var numberOfSimultaneousMissions: Int, private var executor: ExecutorService) {
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

    private fun initMissions(createSharedNetworkChannel: (missionId: Id) -> NetworkChannel) {
        for (threadIdx in 0 until this.numberOfSimultaneousMissions) {
            val missionId = Utils.generateUUID();
            val mission = Mission(missionId, getRandomizedComponentList(), createSharedNetworkChannel(missionId))

            missionList.add(mission)
        }
    }

    private fun executeMissions() {
        missionList.forEach { executor.execute(it) }
    }

    fun shutdownMissionsWhenTerminated() {
        // busy wait ?
        while (!executor.isTerminated) {}
        println("All the missions have been completed!")
    }

    fun scheduleMissions(createSharedNetworkChannel: (missionId: Id) -> NetworkChannel) {
        // might benefit from a ScheduledExecutorService or a forkJoinPool
        // note that we currently execute as much tasks as the threadPool size so there is no reuse ?
        //initMissions(numberOfSimultaneousMissions, createSharedNetwork)
        this.initMissions(createSharedNetworkChannel)
        this.executeMissions()
    }
}