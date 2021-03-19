package coreClasses.network

import utils.Id
import utils.KBs
import utils.MilliSeconds
import utils.Utils

enum class NetworkBandWidth(val value: KBs) {
    SLOW(0.0025f),
    MEDIUM(2f),
    SPEED(2000f),
}

abstract class NetworkService {
    abstract var networkChannel: NetworkChannel
    protected var networkBandWidth: NetworkBandWidth = NetworkBandWidth.SLOW

    init {
       this.setBestPossibleAvailableNetworkBandWidth()
    }

    fun addHeaderToMessageContent(messageContent: String, sizeMessage: Float, missionId: Id, messageType: MessageType): String {
        return "message size: ${sizeMessage}KBs, messageType: $messageType, missionId: \"${networkChannel.missionId}\", $messageContent"
    }

    fun getPayloadSizeForMessageType(messageType: MessageType): KBs {
       return when {
           messageType.name.endsWith("Stage") || messageType == MessageType.Failure -> Utils.getRandomNumberInRange(0f, 1f)
           messageType == MessageType.Telemetry -> Utils.getRandomNumberInRange(10f, 100f)
           messageType == MessageType.Data -> Utils.getRandomNumberInRange(100f, 100000f)
           messageType == MessageType.SoftwareUpdate -> Utils.getRandomNumberInRange(1000f, 10000f)
           else -> Utils.getRandomNumberInRange(0f, 1f)
       }
    }

    fun simulateTimeToTransferMessage(sizeMessage: KBs, distanceFromEarthQuartile: Int) {
       // try {
       //     Thread.sleep(this.getTotalTimeForAMessageToTransmit(sizeMessage, distanceFromEarthQuartile).toLong() / 10)
       // } catch (e: InterruptedException) {
       //     e.printStackTrace()
       // }
   }
    fun setBestPossibleAvailableNetworkBandWidth() {
        if (Utils.getRandomNumberInRange(0f, 100f) <= 80f) {
            this.networkBandWidth = NetworkBandWidth.SPEED
        } else if (Utils.getRandomNumberInRange(0f, 100f) <= 90f) {
            this.networkBandWidth = NetworkBandWidth.MEDIUM
        } else this.networkBandWidth = NetworkBandWidth.SLOW
    }

    fun getTotalTimeForAMessageToTransmit(payloadSize: KBs, distanceFromEarthQuintile: Int): MilliSeconds {
       return payloadSize / networkBandWidth.value * 1000 * distanceFromEarthQuintile
    }

    abstract fun listenIncommingMessage(): Message?
    abstract suspend fun sendMessage(receivedMessageType: MessageType? = null, messageContent: String, newMessageType: MessageType, distanceFromEarthQuintile: Int = 1)
}
