package in.mobifirst.tagtree.backend;

import java.util.logging.Logger;

public class TagTreeLogger {
    static Logger Log = Logger.getLogger("in.mobifirst.tagtree.backend");

    public static void info(String logMe) {
        Log.info("Running in Thread: " + Thread.currentThread() + " " + logMe);
    }
}
