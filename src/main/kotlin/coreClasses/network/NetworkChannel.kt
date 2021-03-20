package coreClasses.network

import utils.Id
import java.util.concurrent.*

/**
 * a Network is a communication channel between the mission Controller (on Earth),
 * and the various components of a specific Mission.
 *
 *
 * There are three types of deep space communications
 * Networks (2MB/sec with 80% availability, 2KB/sec with 90% availability and 20bits/sec with 99.9%
 * availability)
 */

enum class EmitterType {
    Controller,
    Mission
}

enum class MessageType {
    Data,
    Telemetry,
    SoftwareUpdate,
    BoostStage,
    InterTransitStage,
    TransitStage,
    LandingStage,
    ExplorationStage,
    Failure,
    SuccessfulSoftwareUpdate,
    AbortMission
}


class Message(
    val content: String,
    val emitterType: EmitterType,
    val messageType: MessageType,
    var payloadSize: Float //kb
)

class NetworkChannel(
    var missionId: Id,
    var messageQueue: ConcurrentLinkedQueue<Message> = ConcurrentLinkedQueue()
)
