package in.mobifirst.tagtree.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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

    public static String getDuration(long timestamp) {
        String format = String.format("%%0%dd", 2);
        timestamp = timestamp / 1000;
        String seconds = String.format(format, timestamp % 60);
        String minutes = String.format(format, (timestamp % 3600) / 60);
        String hours = String.format(format, timestamp / 3600);
        String time = hours + ":" + minutes + ":" + seconds;
        return time;
    }

    public static String getDurationInHrsAndMin(int timeDuration) {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm a");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);

        calendar.add(Calendar.MINUTE, timeDuration);
        return sfd.format(calendar.getTime());
    }

    public static Date getDate(String dateString) {
        Date date;
        SimpleDateFormat sfd = new SimpleDateFormat("dd-MM-yyyy");
        try {
            date = sfd.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            date = null;
        }
        return date;
    }

    public static String convertDoubleToTimeString(Double aDouble) {
        aDouble = aDouble * 10;
        int startInt = aDouble.intValue();

        int hr = startInt / 10;
        int min = startInt % 10;
        if (min == 5) {
            min = 30;
        }

        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hr);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);

        return sfd.format(calendar.getTime());
    }

    public static String getHrMin(long timestamp) {
        SimpleDateFormat sfd = new SimpleDateFormat("HH:mm");
        return sfd.format(new Date(timestamp));
    }

    public static long convertDoubleToTime(Double aDouble) {
        aDouble = aDouble * 10;
        int startInt = aDouble.intValue();

        int hr = startInt / 10;
        int min = startInt % 10;
        if (min == 5) {
            min = 30;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hr);
        calendar.set(Calendar.MINUTE, min);
        calendar.set(Calendar.SECOND, 0);

        return calendar.getTimeInMillis();
    }
}
