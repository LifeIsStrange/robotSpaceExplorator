package utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class Utils {
    public static void createNewFileIfNotExists(String filename) {
        try {
            File fileObject = new File(filename);
            if (fileObject.createNewFile()) {
                // NTD
            } else {
                System.out.println("File " + filename + " already exists.");
            }
        } catch (IOException e) {
            System.err.println("A file error occured.");
            e.printStackTrace();
        }
    }

    public static void deleteFileIfExists(String filename) {
        File fileObject = new File(filename);

        if (fileObject.delete()) {
            // NTD
        } else {
            System.out.println("Failed to delete the file " + filename + ".");
        }
    }

    public static String defaultLogFileName = "output.dat";

    public static void log(String message) {
        System.out.println(message);

        try {
            Files.writeString(Paths.get(defaultLogFileName), message, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("A file error occured while appending the text.");
            e.printStackTrace();
        }
    }

    public static float getRandomNumberInRange(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }
}

