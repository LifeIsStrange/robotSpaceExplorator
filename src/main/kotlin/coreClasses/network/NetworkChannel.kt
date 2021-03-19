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
// The communication networks for a mission are a shared resource used by all mission
// components, but each mission has its own network.

enum class EmitterType {
    Controller,
    Mission
}

// data (image etc) lourd pas fréquent
// télémétrie (log) fréquent
// software update
// stage 1kb

enum class MessageType {
    Data,
    Telemetry,
    SoftwareUpdate,
    BoostStage,
    InterTransit,
    TransitStage,
    LandingStage,
    ExplorationStage,
    Failure,
    SucessfullSoftwareUpdate
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
