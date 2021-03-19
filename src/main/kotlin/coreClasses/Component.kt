package coreClasses

import coreClasses.network.MessageType
import coreClasses.network.NetworkServiceMissionService
import utils.Utils

/**
 * a Mission robot/spacecraft is made of various Components
 */
// todo all components must report telemetry
// data sending is subject to increasing delays as the mission travels further away from Earth
// how to report progress if we sleep
// Network availability can be checked before a message is to be sent and if a network is
// available then it can be used to transmit the full message

// enum class ComponentType {
//    Fuel, Thruster, Instrument, ControlSystem, PowerPlant
// }

sealed class Component() {
    // reports payload size in kilobytes
    // Reports can be telemetry (100-10k bytes, frequent) or
    // data (100k-100MB, periodic)
    var payloadSize = Utils.getRandomNumberInRange(0.1f, 100000f)

    // report rate in hours
    var reportRate = Utils.getRandomNumberInRange(0.30f, 24f * 7f)

    // open lateinit var message: String
    open fun sendMessage(networkServiceMissionService: NetworkServiceMissionService) {}
}

class Fuel(var quantity: Int = 1000) : Component() {
    override fun sendMessage(networkServiceMissionService: NetworkServiceMissionService) {
        networkServiceMissionService.sendMessage(messageContent = "quantity fuel remaining $quantity", newMessageType = MessageType.Telemetry)
    }
}

// TODO randomizer

class Thruster(var power: Int = 0, var damageLevel: Int = 0, var heatLevel: Int? = null, var isOn: Boolean = false
) : Component() {
    override fun sendMessage(networkServiceMissionService: NetworkServiceMissionService) {
        networkServiceMissionService.sendMessage(messageContent = "power current level $power", newMessageType = MessageType.Telemetry)
    }
}
class Instrument() : Component() {
    override fun sendMessage(networkServiceMissionService: NetworkServiceMissionService) {
        networkServiceMissionService.sendMessage(messageContent = "Instrument are ok !", newMessageType = MessageType.Telemetry)
    }
}

class ControlSystem() : Component() {
    override fun sendMessage(networkServiceMissionService: NetworkServiceMissionService) {
        networkServiceMissionService.sendMessage(messageContent = "No problem to repport !", newMessageType = MessageType.Telemetry)
    }
}

class PowerPlant(val power: Int = 0, val remainingCapacity: Int = 8000, var damageLevel: Int = 0) : Component() {
    override fun sendMessage(networkServiceMissionService: NetworkServiceMissionService) {
        networkServiceMissionService.sendMessage(messageContent = "Power : $power, remaining capacity: $remainingCapacity and damageLevel: $damageLevel", newMessageType = MessageType.Data)
    }
}
