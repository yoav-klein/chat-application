package chat.common.util;

public class Logger {
    public static void info(String message) {
        System.out.println("INFO: " + message);
    }
    
    public static void debug(String message) {
        System.out.println("DEBUG: " + message);
    }

    public static void error(String message) {
        System.out.println("ERROR: " + message);
    }

    public static void warning(String message) {
        System.out.println("WARNING: " + message);
    }
}
