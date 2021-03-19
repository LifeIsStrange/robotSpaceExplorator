package coreClasses.network

import utils.Id
import utils.Utils

enum class NetworkBandWidth(value: Int) {
    SLOW(20),
    MEDIUM(16000),
    SPEED(16000000),
}

abstract class NetworkService {
    abstract var networkChannel: NetworkChannel
    protected var networkBandWidth: NetworkBandWidth = NetworkBandWidth.SLOW

    init {
       this.setBestPossibleAvailableNetworkBandWidth()
    }

    fun addHeaderToMessageContent(messageContent: String, sizeMessage: Float, missionId: Id): String {
        return "message size: $sizeMessage, missionId: ${networkChannel.missionId}, $messageContent"
    }

    fun getPayloadSizeForMessageType(messageType: MessageType): Float {
       return when {
           messageType.name.endsWith("Stage") -> Utils.getRandomNumberInRange(0f, 1f)
           messageType == MessageType.Telemetry -> Utils.getRandomNumberInRange(10f, 100f)
           messageType == MessageType.Data -> Utils.getRandomNumberInRange(100f, 100000f)
           messageType == MessageType.SoftwareUpdate -> Utils.getRandomNumberInRange(1000f, 10000f)
           else -> throw Exception("Unreachable")
       }
    }
    fun setBestPossibleAvailableNetworkBandWidth() {
        if (Utils.getRandomNumberInRange(0f, 100f) <= 80f) {
            this.networkBandWidth = NetworkBandWidth.SPEED
        } else if (Utils.getRandomNumberInRange(0f, 100f) <= 90f) {
            this.networkBandWidth = NetworkBandWidth.MEDIUM
        } else this.networkBandWidth = NetworkBandWidth.SLOW
    }
    abstract fun listenIncommingMessage(): Message?
    abstract fun sendMessage(receivedMessageType: MessageType? = null, messageContent: String, newMessageType: MessageType)
}
