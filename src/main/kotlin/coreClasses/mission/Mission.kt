package coreClasses.mission

import coreClasses.*
import coreClasses.network.MessageType
import coreClasses.network.NetworkChannel
import coreClasses.network.NetworkServiceMissionService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import utils.ANSI_RED
import utils.ANSI_RESET
import utils.Id
import utils.Utils
import java.util.concurrent.atomic.AtomicReference

/**
 * this class is responsible for the scheduling of a Mission
 */
// todo get random destination and network
// todo fix fuel mismatch
class Destination(val name: String, val distance: Float)

//TODO change thread name by ID
//TODO log dans output
//TODO change mission à 10
//TODO changer gérer le temps

class Mission(
    val id: Id,
    var componentList: List<Component>,
    private var networkChannel: NetworkChannel,
    private var destination: Destination,
    private var possibleFailureException: AtomicReference<Exception?>
    ) : Runnable {
    private var missionNetworkService = NetworkServiceMissionService(networkChannel)
    var currentDistanceFromController = 0f

    companion object {
        /** time is simulated by considering months as seconds  */
        var defaultMinStageTime = 1000f
        var defaultMaxStageTime = 5000f
    }
    var startTime = Utils.getRandomNumberInRange(100f, 3000f)
    lateinit var missionId: String

    override fun run() {
        try {
            this.missionId = Thread.currentThread().name
            println(Thread.currentThread().name + " Mission starting.")
            println("---------")
            scheduleStages()
        } catch (e: Exception) {
            System.err.println("je catch l'erreur et je la fou dans l'attmic")
            this.possibleFailureException.set(e)
        }
    }

    private fun sendMessageIfFailure() {
        if (Utils.getRandomNumberInRange(0f, 100f) <= 10f) {
            GlobalScope.launch {
                missionNetworkService.sendMessage(
                    messageContent = "stage transition failed of mission $missionId",
                    newMessageType = MessageType.Failure
                )
            }
            var msg = this.missionNetworkService.listenIncommingMessage()
            if (Utils.getRandomNumberInRange(0f, 100f) > 25f) {
                println("abort mission, update fail")
                throw Exception("Mission $missionId abort, update non successfull")
            } else {
                GlobalScope.launch {
                    missionNetworkService.sendMessage(
                        messageContent = "Mission $missionId has sucessfully recovered from the software update",
                        newMessageType = MessageType.SucessfullSoftwareUpdate
                    )
                }
                //todo dire success
            }
        }
    }

    private fun degradeAndSendComponentsMessages() {
        this.componentList.forEach {
            GlobalScope.launch {
                it.degradeComponent()
                it.sendMessage(missionNetworkService)
            }
        }
    }
    private fun scheduleStages() {
        this.boostStage()
        this.degradeAndSendComponentsMessages()
        this.sendMessageIfFailure()
        this.transitStage()
        this.sendMessageIfFailure()
        this.landingStage()
        this.degradeAndSendComponentsMessages()
        this.sendMessageIfFailure()
        this.explorationStage()
    }

    private fun boostStage() {
        println(Thread.currentThread().name + " entering boost stage..")
        GlobalScope.launch {
           missionNetworkService.sendMessage(
                messageContent = "${Thread.currentThread().name} terminating boost stage",
                newMessageType = MessageType.BoostStage
            )
        }
        // this.networkChannel.messageQueue.offer(Message(content = "${Thread.currentThread().name} terminating boost stage", EmitterType.Mission, MessageType.Boost))
        this.missionNetworkService.listenIncommingMessage()
    }
    // A variable burst of reports and
    // commands are sent at the transition between mission stages.
    // There are a variable number of types
    // of commands and reports for each mission.
    private fun transitStage() {
        val transitStepTime = Utils.getRandomNumberInRange(defaultMinStageTime, defaultMaxStageTime) / 4
        println(Thread.currentThread().name + " entering interplanetary transit stage..")
        val destinationDistanceQuartile = destination.distance / 4f

        GlobalScope.launch {
            Utils.delay(transitStepTime)
            currentDistanceFromController += destinationDistanceQuartile
            missionNetworkService.sendMessage(messageContent = "Mission: $missionId $ANSI_RED inter transit stage 1 $ANSI_RESET in progress: we are at ${currentDistanceFromController} millions km from the earth, we are at ${destination.distance - currentDistanceFromController} millions kms from destination: ${destination.name}", newMessageType = MessageType.InterTransit, distanceFromEarthQuintile = 2)
            degradeAndSendComponentsMessages()
            Utils.delay(transitStepTime)
            currentDistanceFromController += destinationDistanceQuartile
            missionNetworkService.sendMessage(messageContent = "Mission: $missionId $ANSI_RED inter transit stage 2 $ANSI_RESET in progress, middle of the mission: we are at ${currentDistanceFromController} millions km from the earth, we are at ${destination.distance - currentDistanceFromController} millions kms from destination: ${destination.name}", newMessageType = MessageType.InterTransit, distanceFromEarthQuintile = 3)
            degradeAndSendComponentsMessages()
            Utils.delay(transitStepTime)
            currentDistanceFromController += destinationDistanceQuartile
            missionNetworkService.sendMessage(messageContent = "Mission: $missionId $ANSI_RED inter transit stage 3 $ANSI_RESET in progress: we are at ${currentDistanceFromController} millions km from the earth, we are at ${destination.distance - currentDistanceFromController} millions kms from destination: ${destination.name}", newMessageType = MessageType.InterTransit,  distanceFromEarthQuintile = 4)
            degradeAndSendComponentsMessages()
            Utils.delay(transitStepTime)
            currentDistanceFromController += destinationDistanceQuartile
            missionNetworkService.sendMessage(messageContent = "Mission: $missionId $ANSI_RED transit stage ended: $ANSI_RESET we are at ${currentDistanceFromController} millions km from the earth, destination: ${destination.name} has been reached!", newMessageType = MessageType.TransitStage, distanceFromEarthQuintile = 5)
        }
        this.missionNetworkService.listenIncommingMessage()
    }

    private fun landingStage() {
        println(Thread.currentThread().name + " entering landing stage..")
        GlobalScope.launch {
            missionNetworkService.sendMessage(
                messageContent = "${Thread.currentThread().name} terminating landing stage",
                newMessageType = MessageType.LandingStage,
                distanceFromEarthQuintile = 5
            )
        }
        this.missionNetworkService.listenIncommingMessage()
    }

    // Time can simulated by allowing a fixed ratio of wall clock time to mission time eg 1 sec : 1
    // month.
    // When waiting a mission 'sleeps'.
    private fun explorationStage() {
        println(Thread.currentThread().name + " entering exploration stage..")
        try {
            Thread.sleep(
                Utils.getRandomNumberInRange(defaultMinStageTime, defaultMaxStageTime)
                    .toLong()
            )
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        GlobalScope.launch {
            missionNetworkService.sendMessage(
                messageContent = "${Thread.currentThread().name} terminating exploration stage",
                newMessageType = MessageType.ExplorationStage,
                distanceFromEarthQuintile = 5
            )
        }
        // this.networkChannel.messageQueue.offer(Message(content = "${Thread.currentThread().name} terminating transit stage", EmitterType.Mission, MessageType.Exploration))
    }
}
