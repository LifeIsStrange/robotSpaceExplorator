public class robotSpaceExplorator {
    public static void main(String[] args) {
        Utils.deleteFileIfExists(Utils.defaultLogFileName);
        Utils.createNewFileIfNotExists(Utils.defaultLogFileName);
        Utils.log("test");
    }
}
