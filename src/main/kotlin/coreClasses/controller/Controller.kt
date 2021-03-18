package coreClasses.controller

import coreClasses.Network
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import utils.Id
import java.util.concurrent.Executors

class Controller() {
    private var networkMissionList = mutableListOf<Network>()
    private var numberOfSimultaneousMissions = 2

    init {
        MissionScheduller.initMissions(this.numberOfSimultaneousMissions, this::createSharedNetwork)
        GlobalScope.launch {
           executeConcurrentNetworkMissionSchedulers()
        }
        MissionScheduller.scheduleMissions(this.numberOfSimultaneousMissions, this::createSharedNetwork)
    }

    fun chichon() {

    }
    private suspend fun executeConcurrentNetworkMissionSchedulers() {
        val networkSchedullers = this.networkMissionList.map { ControllerNetworkMissionScheduller(it) }
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