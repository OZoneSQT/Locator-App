package dk.seahawk.locator.util;

import android.util.Log;

/**
 * Exception handler utility for logging and displaying exceptions
 */
public class MyExceptionHandler {

    private static final String TAG = "Locator";

    /**
     * Handle exceptions by logging them
     */
    public static void handleException(Exception e) {
        if (e != null) {
            Log.e(TAG, "Exception occurred: " + e.getMessage(), e);
            e.printStackTrace();
        }
    }

    /**
     * Push exception message to log
     */
    public static void pushExceptionMessage(String message) {
        Log.e(TAG, message);
        System.err.println(message);
    }
}


