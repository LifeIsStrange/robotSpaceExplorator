package utils

import kotlinx.coroutines.delay
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

typealias Id = String
typealias MilliSeconds = Float
typealias KBs = Float

public const val ANSI_RESET = "\u001B[0m"
public const val ANSI_BLACK = "\u001B[30m"
public const val ANSI_RED = "\u001B[31m"
public const val ANSI_GREEN = "\u001B[32m"
public const val ANSI_YELLOW = "\u001B[33m"
public const val ANSI_BLUE = "\u001B[34m"
public const val ANSI_PURPLE = "\u001B[35m"
public const val ANSI_CYAN = "\u001B[36m"
public const val ANSI_WHITE = "\u001B[37m"


object Utils {
    var defaultLogFileName = "output.dat"

    fun createNewFileIfNotExists(filename: String) {
        try {
            val fileObject = File(filename)
            if (fileObject.createNewFile()) {
                // NTD
            } else {
                println("File $filename already exists.")
            }
        } catch (e: IOException) {
            System.err.println("A file error occured.")
            e.printStackTrace()
        }
    }

    fun deleteFileIfExists(filename: String) {
        val fileObject = File(filename)
        if (fileObject.delete()) {
            // NTD
        } else {
            println("Failed to delete the file $filename.")
        }
    }

    fun log(message: String?) {
        println(message)
        try {
            Files.writeString(Paths.get(defaultLogFileName), message, StandardOpenOption.APPEND)
        } catch (e: IOException) {
            System.err.println("A file error occured while appending the text.")
            e.printStackTrace()
        }
    }

    fun getRandomNumberInRange(min: Float, max: Float): Float {
        return (Math.random() * (max - min) + min).toFloat()
    }

    fun generateUUID(): String {
        return java.util.UUID.randomUUID().toString().takeLast(4)
    }

    fun sleep(time: MilliSeconds) {
        try {
            Thread.sleep(time.toLong())
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    suspend fun delay(time: MilliSeconds) {
        delay(time.toLong())
    }

}
inline fun <reified T : Enum<T>> T.next(): T {
    val values = enumValues<T>()
    val nextOrdinal = (ordinal + 1) % values.size
    return values[nextOrdinal]
}
