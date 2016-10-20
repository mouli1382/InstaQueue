package in.gm.instaqueue.model;

public class Token {

    private String storeId;
    private String phoneNumber;
    private long tokenNumber;
    private String timestamp;
    private int status;
    private int buzzCount;

    public enum Status {
        ISSUED, READY, CANCELLED, COMPLETED
    }

    ;
    private String counterName;
    public Token() {
    }

    public Token(String storeId, String phoneNumber, long tokenNumber, String timestamp, String counterName) {
        this.storeId = storeId;
        this.phoneNumber = phoneNumber;
        this.tokenNumber = tokenNumber;
        this.timestamp = timestamp;
        this.status = Status.ISSUED.ordinal();
        this.counterName = counterName;
        this.buzzCount = 0;
    }

    public boolean needsBuzz() {
        if (status == Status.READY.ordinal()) {
            return true;
        }
        return false;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getTokenNumber() {
        return tokenNumber;
    }

    public void setTokenNumber(long tokenNumber) {
        this.tokenNumber = tokenNumber;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String counterName) {
        this.counterName = counterName;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public int getBuzzCount() {
        return buzzCount;
    }

    public void setBuzzCount(int buzzCount) {
        this.buzzCount = buzzCount;
    }
}
