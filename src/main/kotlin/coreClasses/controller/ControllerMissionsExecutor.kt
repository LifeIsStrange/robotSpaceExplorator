package coreClasses.controller

import coreClasses.Component
import coreClasses.mission.Destination
import coreClasses.mission.Mission
import coreClasses.network.NetworkChannel
import utils.Id
import utils.Range
import utils.Utils
import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicReference
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

var possibleDestinationList = listOf<Destination>(
    Destination("Moon", 0.384f),
    Destination("Mars", 246f),
    Destination("Venus", 257f),
    Destination("Neptune", 4623f),
    Destination("Mercure", 167f),
    Destination("Sun", 149f),
    Destination("Jupiter", 870f),
    Destination("Saturn", 746f),
    Destination("Uranus", 3069f),
)

class ControllerMissionsExecutor(private var numberOfSimultaneousMissions: Int, private var executor: ExecutorService) {
    private var missionList: MutableList<Mission> = mutableListOf()
    val exeptionMissionFailedList = List<AtomicReference<Exception?>>(numberOfSimultaneousMissions) { AtomicReference(null)}

    private fun getRandomizedComponentList(): List<Component> {
        val componentList = mutableListOf<Component>()

        for (componentType in Component::class.sealedSubclasses) {
            val numberOfOccurrencesOfThisComponent = Utils.getRandomNumberInRange(1f, 10f).toInt()
            for (occurrenceIdx in Range.range(numberOfOccurrencesOfThisComponent)) {
                componentList.add(componentType.createInstance())
            }
        }
        return componentList.shuffled()
    }

    private fun initMissions(createSharedNetworkChannel: (missionId: Id) -> NetworkChannel) {
        for (threadIdx in 0 until this.numberOfSimultaneousMissions) {
            val missionId = Utils.generateUUID()
            val mission = Mission(
                missionId,
                getRandomizedComponentList(),
                createSharedNetworkChannel(missionId),
                possibleDestinationList[Utils.getRandomNumberInRange(0f, possibleDestinationList.lastIndex.toFloat()).toInt()],
                this.exeptionMissionFailedList[threadIdx]
            )
            missionList.add(mission)
        }
    }

    private fun executeMissions() {
            missionList.forEach { executor.execute(it) }
    }

    fun shutdownMissionsWhenTerminated() {
        // busy wait ?
        while (!executor.isTerminated) {}

        //if (this.exeptionMissionFailedList.reduce {}

        println("All the missions have been completed!")
        this.exeptionMissionFailedList.forEach {
            val exeption = it.get()
            if (exeption != null) {
               System.err.println("on catch yES sir: voici l'exption: ")
               exeption.printStackTrace()
            }
        }
    }

    fun scheduleMissions(createSharedNetworkChannel: (missionId: Id) -> NetworkChannel) {
        // might benefit from a ScheduledExecutorService or a forkJoinPool
        // note that we currently execute as much tasks as the threadPool size so there is no reuse ?
        // initMissions(numberOfSimultaneousMissions, createSharedNetwork)
        this.initMissions(createSharedNetworkChannel)
        this.executeMissions()
    }
}
