package org.openjfx.rexpotify;

import java.io.*;
import java.util.Properties;

public class AppConfig {

    // Specify the location of the config file outside the JAR
    //public static final String CONFIG_FILE_PATH = "config" + File.separator + "config.properties";
    //Test on project
    public static final String CONFIG_FILE_PATH = "C:\\Users\\Avishka\\Documents\\JavaApplications\\Rexpotify\\src\\main\\resources\\config.properties";

    public static void saveConfig(String keyPrefix, String songName, String songLink, String songDest) {
        try (InputStream input = new FileInputStream(CONFIG_FILE_PATH)) {
            Properties prop = new Properties();

            // Load existing properties (if any)
            prop.load(input);

            // Check if the songName already exists in the config
            if (!isSongAlreadyExists(prop, songName)) {
                // Find the next available index for the new song
                int index = 1;
                while (prop.containsKey(keyPrefix + index + ".songName") || prop.containsKey(keyPrefix + index + ".songLink") || prop.containsKey(keyPrefix + index + ".dest")) {
                    index++;
                }

                // Set the new properties for the song
                prop.setProperty(keyPrefix + index + ".songName", songName);
                prop.setProperty(keyPrefix + index + ".songLink", songLink);
                prop.setProperty(keyPrefix + index + ".dest", songDest);

                // Save the updated properties file
                try (OutputStream output = new FileOutputStream(CONFIG_FILE_PATH)) {
                    prop.store(output, null);
                    System.out.println("Configuration saved successfully.");
                }
            } else {
                System.out.println("Song already exists. Not saving.");
            }

        } catch (IOException io) {
            io.printStackTrace();
            System.out.println("Error saving configuration.");
        }
    }

    private static boolean isSongAlreadyExists(Properties prop, String songName) {
        for (String key : prop.stringPropertyNames()) {
            if (key.endsWith(".songName") && prop.getProperty(key).equals(songName)) {
                return true;
            }
        }
        return false;
    }
}
