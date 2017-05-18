package in.mobifirst.tagtree.model;

import java.util.List;

public class StoreServiceMetrics {
    private long avgTurnAroundTime;
    private long avgWaitTime;
    private long avgBurstTime;

    //Maintain the appointmentIds here so that some action can be performed if the business wants to.
    private List<String> servedAppointments;
    private List<String> cancelledAppointments;
    private List<String> noShowAppointments;

    public StoreServiceMetrics(long avgTurnAroundTime, long avgWaitTime, long avgBurstTime, List<String> servedAppointments, List<String> cancelledAppointments, List<String> noShowAppointments) {
        this.avgTurnAroundTime = avgTurnAroundTime;
        this.avgWaitTime = avgWaitTime;
        this.avgBurstTime = avgBurstTime;
        this.servedAppointments = servedAppointments;
        this.cancelledAppointments = cancelledAppointments;
        this.noShowAppointments = noShowAppointments;
    }

    public long getAvgTurnAroundTime() {
        return avgTurnAroundTime;
    }

    public void setAvgTurnAroundTime(long avgTurnAroundTime) {
        this.avgTurnAroundTime = avgTurnAroundTime;
    }

    public long getAvgWaitTime() {
        return avgWaitTime;
    }

    public void setAvgWaitTime(long avgWaitTime) {
        this.avgWaitTime = avgWaitTime;
    }

    public long getAvgBurstTime() {
        return avgBurstTime;
    }

    public void setAvgBurstTime(long avgBurstTime) {
        this.avgBurstTime = avgBurstTime;
    }

    public List<String> getServedAppointments() {
        return servedAppointments;
    }

    public void setServedAppointments(List<String> servedAppointments) {
        this.servedAppointments = servedAppointments;
    }

    public List<String> getCancelledAppointments() {
        return cancelledAppointments;
    }

    public void setCancelledAppointments(List<String> cancelledAppointments) {
        this.cancelledAppointments = cancelledAppointments;
    }

    public List<String> getNoShowAppointments() {
        return noShowAppointments;
    }

    public void setNoShowAppointments(List<String> noShowAppointments) {
        this.noShowAppointments = noShowAppointments;
    }
}
