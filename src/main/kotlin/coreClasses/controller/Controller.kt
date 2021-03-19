package coreClasses.controller

import coreClasses.Network
import utils.Id
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class Controller() {
    private var networkMissionList = mutableListOf<Network>()
    private var numberOfSimultaneousMissions = 2
    private var missionsExecutor: ExecutorService = Executors.newFixedThreadPool(this.numberOfSimultaneousMissions)
    private var controllerNetworkMissionSchedullerExecutor = Executors.newFixedThreadPool(this.numberOfSimultaneousMissions)
    private var missionScheduller: MissionScheduller = MissionScheduller(this.numberOfSimultaneousMissions, missionsExecutor)

    init {
        this.missionScheduller.scheduleMissions(this::createSharedNetwork)
        missionsExecutor.shutdown()
        this.executeConcurrentNetworkMissionSchedulers()
        this.shutdownAllThreadServicesWhenTerminated()
    }

    private fun shutdownAllThreadServicesWhenTerminated() {
        this.missionScheduller.shutdownMissionsWhenTerminated()
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