package coreClasses

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

class Controller {
    var missionList :MutableList<Runnable> = mutableListOf();

    companion object {
        // 10 is the minimum per the requirements
        var defaultNumberOfSimultaneousMissions = 2
    }

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

    private fun initMissions(numberOfSimultaneousMissions: Int) {
        for (threadIdx in 0..numberOfSimultaneousMissions) {
            val mission =  Mission(getRandomizedComponentList())
            this.missionList.add(mission)
        }
    }

    private fun executeMissions(executor: ExecutorService) {
        this.missionList.forEach { executor.execute(it) }
    }
    fun scheduleMissions(numberOfSimultaneousMissions: Int) {
        // might benefit from a ScheduledExecutorService or a forkJoinPool
        // note that we currently execute as much tasks as the threadPool size so there is no reuse ?
        this.initMissions(numberOfSimultaneousMissions)
        val executor = Executors.newFixedThreadPool(numberOfSimultaneousMissions)
        this.executeMissions(executor)
        executor.shutdown()
        // busy wait ?
        while (!executor.isTerminated) {}
        println("All the missions have been completed!")
    }
}