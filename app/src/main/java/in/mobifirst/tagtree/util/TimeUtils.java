package in.mobifirst.tagtree.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {

    public static String getDate(long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
        return sfd.format(new Date(timestamp));
    }

    public static String getTime(long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm:ss");
        return sfd.format(new Date(timestamp));
    }

    public static String getDateTime(long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        return sfd.format(new Date(timestamp));
    }

}
