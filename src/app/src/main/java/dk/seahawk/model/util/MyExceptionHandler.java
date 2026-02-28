package dk.seahawk.model.util;

public class MyExceptionHandler {
    public static void handle(Exception e) {
        e.printStackTrace();
    }

    public void pushExceptionMessage(String message) {
        System.err.println(message);
    }
}

