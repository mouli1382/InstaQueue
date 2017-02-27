package io.swagger.api.impl;

import java.util.logging.Logger;

public class TTLogger {
    static Logger Log = Logger.getLogger(TTLogger.class.getSimpleName());

    public static void info(String logMe) {
        Log.info("Running in Thread: " + Thread.currentThread() + " " + logMe);
    }
}
