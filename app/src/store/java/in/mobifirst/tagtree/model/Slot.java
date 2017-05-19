package in.mobifirst.tagtree.model;

public class Slot {

    private String day;
    private int daysMask;
    private String timeSlots;

    public Slot() {
    }

    public Slot(String day, int daysMask, String timeSlots) {
        this.day = day;
        this.daysMask = daysMask;
        this.timeSlots = timeSlots;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getDaysMask() {
        return daysMask;
    }

    public void setDaysMask(int daysMask) {
        this.daysMask = daysMask;
    }

    public String getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(String timeSlots) {
        this.timeSlots = timeSlots;
    }
}
