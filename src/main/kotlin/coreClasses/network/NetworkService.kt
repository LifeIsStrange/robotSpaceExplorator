package coreClasses.network

import coreClasses.Message
import coreClasses.MessageStageType
import coreClasses.NetworkChannel
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

    fun setBestPossibleAvailableNetworkBandWidth() {
        if (Utils.getRandomNumberInRange(0f, 100f) <= 80f) {
            this.networkBandWidth = NetworkBandWidth.SPEED
        } else if (Utils.getRandomNumberInRange(0f, 100f) <= 90f) {
            this.networkBandWidth = NetworkBandWidth.MEDIUM
        } else this.networkBandWidth = NetworkBandWidth.SLOW
    }
    abstract fun listenIncommingMessage(): Message?
    abstract fun sendMessage(receivedMessageType: MessageStageType? = null, messageContent: String, newMessageType: MessageStageType)
}
