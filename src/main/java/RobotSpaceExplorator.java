import coreClasses.Controller;
import utils.Utils;

public class RobotSpaceExplorator {
    private static void createConfigFile() {
        Utils.deleteFileIfExists(Utils.defaultLogFileName);
        Utils.createNewFileIfNotExists(Utils.defaultLogFileName);
    }

    public static void main(String[] args) {
        createConfigFile();
        Utils.log("test");

        var controller = new Controller();
        controller.scheduleMissions(Controller.defaultNumberOfSimultaneousMissions);
    }
}
