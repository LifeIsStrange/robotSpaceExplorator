package coreClasses.controller

import coreClasses.component.Component
import coreClasses.mission.Destination
import coreClasses.mission.Mission
import coreClasses.network.NetworkChannel
import utils.Id
import utils.Range
import utils.Utils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicReference
import kotlin.reflect.full.createInstance

class Controller() {
    private var networkMissionList = mutableListOf<NetworkChannel>()
    private var numberOfSimultaneousMissions = Utils.getRandomNumberInRange(2f, 15f).toInt()
    private var missionsExecutor: ExecutorService = Executors.newFixedThreadPool(this.numberOfSimultaneousMissions)
    private var controllerNetworkMissionSchedullerExecutor = Executors.newFixedThreadPool(this.numberOfSimultaneousMissions)
    private var controllerMissionsExecutor: ControllerMissionsExecutor = ControllerMissionsExecutor(this.numberOfSimultaneousMissions, missionsExecutor)

    init {
        this.controllerMissionsExecutor.scheduleMissions(this::createSharedNetwork)
        missionsExecutor.shutdown() // this doesn't mean missions will shutdown, just that they are allowed to once they've outlived their lifetimes
        this.executeConcurrentNetworkMissionSchedulers()
        this.shutdownAllThreadServicesWhenTerminated()
    }

    private fun shutdownAllThreadServicesWhenTerminated() {
        this.controllerMissionsExecutor.shutdownMissionsWhenTerminated()
        this.shutdownNetworkMissionSchedulersWhenTerminated()
    }

    private fun shutdownNetworkMissionSchedulersWhenTerminated() {
        this.controllerNetworkMissionSchedullerExecutor.shutdown()
        while (!this.controllerNetworkMissionSchedullerExecutor.isTerminated) {}
    }

    private fun executeConcurrentNetworkMissionSchedulers() {
        val networkSchedullers = this.networkMissionList.map { ControllerNetworkMissionScheduller(it, missionsExecutor) }
        networkSchedullers.forEach { this.controllerNetworkMissionSchedullerExecutor.execute(it) }
    }

    private fun createSharedNetwork(missionId: Id): NetworkChannel {
        var network = NetworkChannel(missionId)

        this.networkMissionList.add(network)
        return network
    }
}

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
    val exeptionMissionFailedList = List<AtomicReference<Exception?>>(numberOfSimultaneousMissions) { AtomicReference(null) }

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
        // wait
        while (!executor.isTerminated) {}

        if (!this.exeptionMissionFailedList.any { it.get() is java.lang.Exception }) {
            Utils.log("All the missions have been completed!")
        }
        this.exeptionMissionFailedList.forEach {
            val exeption = it.get()
            if (exeption != null) {
                System.err.println("on catch: voici l'exption: ")
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
