package coreClasses.component

import coreClasses.network.MessageType
import coreClasses.network.NetworkServiceMissionService
import utils.Id
import utils.Utils

/**
 * a Mission robot/spacecraft is made of various Components
 */

sealed class Component() {
    // reports payload size in kilobytes
    // Reports can be telemetry (100-10k bytes, frequent) or
    // data (100k-100MB, periodic)

    var id: Id = Utils.generateUUID()

    var payloadSize = Utils.getRandomNumberInRange(0.1f, 100000f)

    // report rate in hours
    var reportRate = Utils.getRandomNumberInRange(0.30f, 24f * 7f)

    open suspend fun sendMessage(networkServiceMissionService: NetworkServiceMissionService, currentStage: String?) {}

    fun degradeComponentProperty(value: Int): Int {
        val newVal = value / Utils.getRandomNumberInRange(4f, 9f).toInt();
        return value - newVal
    }
    open fun degradeComponent() {}
}

class Fuel(var quantity: Int = 1000) : Component() {
    override suspend fun sendMessage(
        networkServiceMissionService: NetworkServiceMissionService,
        currentStage: String?
    ) {
        networkServiceMissionService.sendMessage(messageContent = "Component Fuel \"$id\", Stage \"$currentStage\": quantity remaining in the tank is : $quantity", newMessageType = MessageType.Telemetry)
    }

    override fun degradeComponent() {
        this.quantity -= this.degradeComponentProperty(this.quantity)
    }

}

class Thruster(var power: Int = 0, var damageLevel: Int = 0, var heatLevel: Int = 1000, var isOn: Boolean = false
) : Component() {
    override suspend fun sendMessage(
        networkServiceMissionService: NetworkServiceMissionService,
        currentStage: String?
    ) {
        networkServiceMissionService.sendMessage(messageContent = "Component Thruster \"$id\", Stage \"$currentStage\": power current level $power, damage level: $damageLevel, heat level: $heatLevel degre celsus", newMessageType = MessageType.Telemetry)
    }

    override fun degradeComponent() {
        this.power -= this.degradeComponentProperty(this.power)
        this.damageLevel += Utils.getRandomNumberInRange(30f, 132f).toInt()
        this.heatLevel += Utils.getRandomNumberInRange(-132f, 132f).toInt()
    }
}
class Instrument() : Component() {
    override suspend fun sendMessage(
        networkServiceMissionService: NetworkServiceMissionService,
        currentStage: String?
    ) {
        networkServiceMissionService.sendMessage(messageContent = "Component Instrument \"$id\", Stage \"$currentStage\": is ok !", newMessageType = MessageType.Telemetry)
    }
}

class ControlSystem() : Component() {
    override suspend fun sendMessage(
        networkServiceMissionService: NetworkServiceMissionService,
        currentStage: String?
    ) {
        networkServiceMissionService.sendMessage(messageContent = "Component ControlSystem \"$id\", Stage \"$currentStage\": has no problem to report!", newMessageType = MessageType.Telemetry)
    }
}

class PowerPlant(var power: Int = 0, var remainingCapacity: Int = 8000, var damageLevel: Int = 0) : Component() {
    override suspend fun sendMessage(
        networkServiceMissionService: NetworkServiceMissionService,
        currentStage: String?
    ) {
        networkServiceMissionService.sendMessage(messageContent = "Component PowerPlant \"$id\", Stage \"$currentStage\": informations: Power : $power, remaining capacity: $remainingCapacity and damageLevel: $damageLevel", newMessageType = MessageType.Data)
    }

    override fun degradeComponent() {
        this.power -= this.degradeComponentProperty(this.power)
        this.remainingCapacity -= this.degradeComponentProperty(this.remainingCapacity)
        this.damageLevel -= this.degradeComponentProperty(this.damageLevel)
    }
}
