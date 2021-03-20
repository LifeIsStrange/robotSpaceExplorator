package coreClasses.mission

import coreClasses.component.Component
import coreClasses.network.MessageType
import coreClasses.network.NetworkChannel
import coreClasses.network.NetworkMissionService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import utils.*
import java.util.concurrent.atomic.AtomicReference

/**
 * this class is responsible for the scheduling of a Mission through network messages with the Controller (Earth)
 */
class Destination(val name: String, val distance: Float)

class Mission(
    val id: Id,
    var componentList: List<Component>,
    private var networkChannel: NetworkChannel,
    private var destination: Destination,
    private var possibleFailureException: AtomicReference<Exception?>
) : Runnable {
    private var missionNetworkService = NetworkMissionService(networkChannel)
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
            // yes this identifier is a simplification for debugging and a thread can actually be shared between multiples tasks
            this.missionId = Thread.currentThread().name
            Utils.log(Thread.currentThread().name + " Mission starting.")
            Utils.log("---------")
            scheduleStages()
        } catch (e: Exception) {
            Utils.log("$ANSI_RED je catch l'erreur in an AtomicReference $ANSI_RESET")
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
                runBlocking {
                    missionNetworkService.sendMessage(messageContent = "abort mission, update fail", newMessageType = MessageType.AbortMission)
                }
                throw Exception("Mission $missionId abort, update non successfull")
            } else {
                GlobalScope.launch {
                    missionNetworkService.sendMessage(
                        messageContent = "Mission $missionId has sucessfully recovered from the software update",
                        newMessageType = MessageType.SuccessfulSoftwareUpdate
                    )
                }
            }
        }
    }

    private fun degradeAndSendComponentsMessages(currentStage: String?) {
        runBlocking {
            componentList.forEach {
                it.degradeComponent()
                it.sendMessage(missionNetworkService, currentStage)
            }
        }
    }

    // A variable burst of reports and
    // commands are sent at the transition between mission stages.
    // There are a variable number of types
    // of commands and reports for each mission.
    private fun scheduleStages() {
        this.boostStage()
        this.degradeAndSendComponentsMessages("after boost stage")
        this.sendMessageIfFailure()

        this.transitStage()
        this.sendMessageIfFailure()

        this.landingStage()
        this.degradeAndSendComponentsMessages("after landing")
        this.sendMessageIfFailure()

        this.explorationStage()
    }

    private fun boostStage() {
        Utils.log(Thread.currentThread().name + " entering boost stage..")
        GlobalScope.launch {
            missionNetworkService.sendMessage(
                messageContent = "${Thread.currentThread().name} terminating boost stage",
                newMessageType = MessageType.BoostStage
            )
        }
        this.missionNetworkService.listenIncommingMessage()
    }

    private fun transitStage() {
        val transitStepTime = Utils.getRandomNumberInRange(defaultMinStageTime, defaultMaxStageTime) / 4
        println(Thread.currentThread().name + " entering interplanetary transit stage..")
        val destinationDistanceQuartile = destination.distance / 4f

        GlobalScope.launch {
            Utils.delay(transitStepTime)
            currentDistanceFromController += destinationDistanceQuartile
            System.err.println("$id 1")
            missionNetworkService.sendMessage(messageContent = "$ANSI_GREEN Mission: $missionId $ANSI_RESET $ANSI_RED inter transit stage 1 $ANSI_RESET $ANSI_GREEN in progress: we are at $currentDistanceFromController millions km from the earth, we are at ${destination.distance - currentDistanceFromController} millions kms from destination: ${destination.name} $ANSI_RESET", newMessageType = MessageType.InterTransitStage, distanceFromEarthQuintile = 2)
            degradeAndSendComponentsMessages("inter transit stage 1")
            Utils.delay(transitStepTime)
            currentDistanceFromController += destinationDistanceQuartile
            System.err.println("$id 2")
            missionNetworkService.sendMessage(messageContent = "$ANSI_GREEN Mission: $missionId $ANSI_RESET $ANSI_RED inter transit stage 2 $ANSI_RESET $ANSI_GREEN in progress, middle of the mission: we are at $currentDistanceFromController millions km from the earth, we are at ${destination.distance - currentDistanceFromController} millions kms from destination: ${destination.name} $ANSI_RESET", newMessageType = MessageType.InterTransitStage, distanceFromEarthQuintile = 3)
            degradeAndSendComponentsMessages("inter transit stage 2")
            Utils.delay(transitStepTime)
            currentDistanceFromController += destinationDistanceQuartile
            System.err.println("$id 3")
            missionNetworkService.sendMessage(messageContent = "$ANSI_GREEN Mission: $missionId $ANSI_RESET $ANSI_RED inter transit stage 3 $ANSI_RESET $ANSI_GREEN in progress: we are at $currentDistanceFromController millions km from the earth, we are at ${destination.distance - currentDistanceFromController} millions kms from destination: ${destination.name} $ANSI_RESET", newMessageType = MessageType.InterTransitStage, distanceFromEarthQuintile = 4)
            degradeAndSendComponentsMessages("inter transit stage 3")
            Utils.delay(transitStepTime)
            currentDistanceFromController += destinationDistanceQuartile
            missionNetworkService.sendMessage(messageContent = "$ANSI_GREEN Mission: $missionId $ANSI_RESET $ANSI_RED transit stage ended: $ANSI_RESET w$ANSI_GREEN e are at $currentDistanceFromController millions km from the earth, destination: ${destination.name} has been reached! $ANSI_RESET", newMessageType = MessageType.TransitStage, distanceFromEarthQuintile = 5)
            System.err.println("-------------------END OF TRANSIT-----------------------")
        }
        this.missionNetworkService.listenIncommingMessage()
    }

    private fun landingStage() {
        Utils.log(Thread.currentThread().name + " entering landing stage..")
        GlobalScope.launch {
            missionNetworkService.sendMessage(
                messageContent = "${Thread.currentThread().name} terminating landing stage",
                newMessageType = MessageType.LandingStage,
                distanceFromEarthQuintile = 5
            )
        }
        this.missionNetworkService.listenIncommingMessage()
    }

    private fun explorationStage() {
        println(Thread.currentThread().name + " entering exploration stage..")
        runBlocking {
            Utils.delay(2000f)
        }
        GlobalScope.launch {
            missionNetworkService.sendMessage(
                messageContent = "${Thread.currentThread().name} terminating exploration stage",
                newMessageType = MessageType.ExplorationStage,
                distanceFromEarthQuintile = 5
            )
        }
    }
}
