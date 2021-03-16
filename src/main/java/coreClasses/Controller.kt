package coreClasses

import utils.Range
import utils.Utils
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

    fun scheduleMissions(numberOfSimultaneousMissions: Int) {
        // might benefit from a ScheduledExecutorService or a forkJoinPool
        // note that we currently execute as much tasks as the threadPool size so there is no reuse ?
        val executor = Executors.newFixedThreadPool(numberOfSimultaneousMissions)

        for (threadIdx in 0..numberOfSimultaneousMissions) {
            val mission: Runnable = Mission(getRandomizedComponentList())
            executor.execute(mission)
        }
        executor.shutdown()
        // busy wait ?
        while (!executor.isTerminated) {}
        println("All the missions have been completed!")
    }
}