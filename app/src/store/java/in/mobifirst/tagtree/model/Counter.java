package in.mobifirst.tagtree.model;

public class Counter {
    private int counterNumber;
    private String counterName;
    private int counterCapacity;

    public Counter(int counterNumber, String counterName, int numberOfTokens) {
        this.counterNumber = counterNumber;
        this.counterName = counterName;
        this.counterCapacity = numberOfTokens;
    }

    public int getCounterNumber() {
        return counterNumber;
    }

    public void setCounterNumber(int counterNumber) {
        this.counterNumber = counterNumber;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public int getCounterCapacity() {
        return counterCapacity;
    }

    public void setCounterCapacity(int counterCapacity) {
        this.counterCapacity = counterCapacity;
    }
}
