package coreClasses

import utils.Id
import java.util.*
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

class Message(
    var content: String,
    var response: String? = null
)

class Network(
    var missionId: Id,
    var messageQueue: ConcurrentLinkedQueue<Message> = ConcurrentLinkedQueue()
)