package utils

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

typealias Id = String;

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
        return java.util.UUID.randomUUID().toString()
    }
}
inline fun <reified T: Enum<T>> T.next(): T {
    val values = enumValues<T>()
    val nextOrdinal = (ordinal + 1) % values.size
    return values[nextOrdinal]
}