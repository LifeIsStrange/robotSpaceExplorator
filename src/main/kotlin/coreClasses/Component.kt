package coreClasses

import utils.Utils

/**
 * a Mission robot/spacecraft is made of various Components
 */
// todo all components must report telemetry
// data sending is subject to increasing delays as the mission travels further away from Earth
// how to report progress if we sleep
// Network availability can be checked before a message is to be sent and if a network is
// available then it can be used to transmit the full message

//enum class ComponentType {
//    Fuel, Thruster, Instrument, ControlSystem, PowerPlant
//}

sealed class Component {
    // reports payload size in kilobytes
    // Reports can be telemetry (100-10k bytes, frequent) or
    // data (100k-100MB, periodic)
    var payloadSize = Utils.getRandomNumberInRange(0.1f, 100000f)

    // report rate in hours
    var reportRate = Utils.getRandomNumberInRange(0.30f, 24f * 7f)

    //open lateinit var message: String
    open fun showMessage() {}
}

data class Fuel(var quantity: Int = 1000): Component() {
    override fun showMessage() {
        //println(this.message.capitalize())
        super.showMessage()
    }
}

// TODO randomizer

data class Thruster(var power: Int = 0, var damageLevel: Int = 0, var heatLevel: Int? = null, var isOn: Boolean = false): Component() {
    override fun showMessage() {
        super.showMessage()
    }
}
class Instrument() : Component() {
    override fun showMessage() {
        super.showMessage()
    }
}

class ControlSystem(): Component() {
    override fun showMessage() {
        super.showMessage()
    }
}

data class PowerPlant(val power: Int = 0, val remainingCapacity: Int = 8000, var damageLevel: Int = 0): Component() {
    override fun showMessage() {
        super.showMessage()
    }
}
