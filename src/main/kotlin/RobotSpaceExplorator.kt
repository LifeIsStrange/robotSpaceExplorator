import coreClasses.controller.Controller
import utils.Utils

object RobotSpaceExplorator {
    fun createConfigFile() {
        Utils.deleteFileIfExists(Utils.defaultLogFileName)
        Utils.createNewFileIfNotExists(Utils.defaultLogFileName)
    }
}

fun main(args: Array<String>) {
    RobotSpaceExplorator.createConfigFile()
    val controller = Controller()
}
