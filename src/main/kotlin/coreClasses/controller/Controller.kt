package coreClasses.controller

import coreClasses.Network
import utils.Id
import java.util.concurrent.Executors

class Controller() {
    private var networkMissionList = mutableListOf<Network>()
    private var numberOfSimultaneousMissions = 2

    init {
        MissionScheduller.scheduleMissions(this.numberOfSimultaneousMissions, this::createSharedNetwork)
        this.executeConcurrentNetworkSchedulers()
    }

    private fun executeConcurrentNetworkSchedulers() {
        val networkSchedullers = this.networkMissionList.map { NetworkScheduller(it) }
        val executor = Executors.newFixedThreadPool(numberOfSimultaneousMissions)
        networkSchedullers.forEach { executor.execute(it) }
        executor.shutdown()
        while (!executor.isTerminated) {}
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