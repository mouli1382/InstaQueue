package in.mobifirst.tagtree.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class Token implements Parcelable {

    private String uId;
    private String storeId;
    private String phoneNumber;
    private long tokenNumber;
    private long timestamp;
    private int status;
    private int buzzCount;
    private String senderPic;
    private String senderName;
    private String areaName;
    private long activatedTokenTime;
    private long tokenFinishTime;
    private String userName;
    private String mappingId;

    private long date;
    private String serviceId;
    private long appointmentTime;

    private String timeRange; //Maintaining 1Hr range for a smoother display.

    public enum Status {
        UNKNOWN, ISSUED, READY, CANCELLED, COMPLETED
    }

    ;

    public Token() {
        // Default constructor required for calls to DataSnapshot.getValue(Token.class)
    }

    public Token(String uId, String storeId, String serviceId, long tokenNumber, long date, long appointmentTime, String timeRange) {
        this.uId = uId;
        this.storeId = storeId;
        this.serviceId = serviceId;
        this.tokenNumber = tokenNumber;
        this.status = Status.UNKNOWN.ordinal();
        this.date = date;
        this.appointmentTime = appointmentTime;
        this.timeRange = timeRange;
    }

    public Token(String uId, String storeId, String phoneNumber, long tokenNumber, String senderPic, String senderName, String areaName, String mappingId, long date) {
        this.uId = uId;
        this.storeId = storeId;
        this.phoneNumber = phoneNumber;
        this.tokenNumber = tokenNumber;
        this.status = Status.ISSUED.ordinal();
        this.buzzCount = 0;
        this.senderPic = senderPic;
        this.senderName = senderName;
        this.areaName = areaName;
        this.mappingId = mappingId;
        this.date = date;
    }

    public boolean needsBuzz() {
        if (status == Status.READY.ordinal()) {
            return true;
        }
        return false;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPic() {
        return senderPic;
    }

    public void setSenderPic(String senderPic) {
        this.senderPic = senderPic;
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

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getActivatedTokenTime() {
        return activatedTokenTime;
    }

    public void setActivatedTokenTime(long timestamp) {
        this.activatedTokenTime = timestamp;
    }

    public long getTokenFinishTime() {
        return tokenFinishTime;
    }

    public void setTokenFinishTime(long tokenFinishTime) {
        this.tokenFinishTime = tokenFinishTime;
    }

    public int getBuzzCount() {
        return buzzCount;
    }

    public void setBuzzCount(int buzzCount) {
        this.buzzCount = buzzCount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMappingId() {
        return mappingId;
    }

    public void setMappingId(String mappingId) {
        this.mappingId = mappingId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public long getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(long appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getTimeRange() {
        return timeRange;
    }

    public void setTimeRange(String timeRange) {
        this.timeRange = timeRange;
    }

    @Exclude
    public boolean isCompleted() {
        return status == Status.COMPLETED.ordinal();
    }

    @Exclude
    public boolean isUnknown() {
        return status == Status.UNKNOWN.ordinal();
    }

    @Exclude
    public boolean isIssued() {
        return status == Status.ISSUED.ordinal();
    }

    @Exclude
    public boolean isActive() {
        return status == Status.READY.ordinal();
    }

    @Exclude
    public boolean isEmpty() {
        return TextUtils.isEmpty(phoneNumber);
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uId", uId);
        result.put("storeId", storeId);
        result.put("phoneNumber", phoneNumber);
        result.put("userName", userName);
        result.put("tokenNumber", tokenNumber);
        result.put("timestamp", ServerValue.TIMESTAMP);
        result.put("status", status);
        result.put("buzzCount", buzzCount);
        result.put("senderName", senderName);
        result.put("senderPic", senderPic);
        result.put("areaName", areaName);
        result.put("activatedTokenTime", activatedTokenTime);
        result.put("tokenFinishTime", tokenFinishTime);
        result.put("mappingId", mappingId);
        result.put("date", date);
        result.put("serviceId", serviceId);
        result.put("appointmentTime", appointmentTime);
        result.put("timeRange", timeRange);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token)) return false;

        Token token = (Token) o;

        if (getTokenNumber() != token.getTokenNumber()) return false;
        if (getTimestamp() != token.getTimestamp()) return false;
        if (getStatus() != token.getStatus()) return false;
        if (getBuzzCount() != token.getBuzzCount()) return false;
        if (getActivatedTokenTime() != token.getActivatedTokenTime()) return false;
        if (getTokenFinishTime() != token.getTokenFinishTime()) return false;
        if (getDate() != token.getDate()) return false;
        if (!getuId().equals(token.getuId())) return false;
        if (!getStoreId().equals(token.getStoreId())) return false;
        if (!getPhoneNumber().equals(token.getPhoneNumber())) return false;
        if (getSenderPic() != null ? !getSenderPic().equals(token.getSenderPic()) : token.getSenderPic() != null)
            return false;
        if (getSenderName() != null ? !getSenderName().equals(token.getSenderName()) : token.getSenderName() != null)
            return false;
        if (getAreaName() != null ? !getAreaName().equals(token.getAreaName()) : token.getAreaName() != null)
            return false;
        if (getUserName() != null ? !getUserName().equals(token.getUserName()) : token.getUserName() != null)
            return false;
        return getMappingId() != null ? getMappingId().equals(token.getMappingId()) : token.getMappingId() == null;

    }

    @Override
    public int hashCode() {
        int result = getuId().hashCode();
        result = 31 * result + getStoreId().hashCode();
        result = 31 * result + getPhoneNumber().hashCode();
        result = 31 * result + (int) (getTokenNumber() ^ (getTokenNumber() >>> 32));
        result = 31 * result + (int) (getTimestamp() ^ (getTimestamp() >>> 32));
        result = 31 * result + getStatus();
        result = 31 * result + getBuzzCount();
        result = 31 * result + (getSenderPic() != null ? getSenderPic().hashCode() : 0);
        result = 31 * result + (getSenderName() != null ? getSenderName().hashCode() : 0);
        result = 31 * result + (getAreaName() != null ? getAreaName().hashCode() : 0);
        result = 31 * result + (int) (getActivatedTokenTime() ^ (getActivatedTokenTime() >>> 32));
        result = 31 * result + (int) (getTokenFinishTime() ^ (getTokenFinishTime() >>> 32));
        result = 31 * result + (getUserName() != null ? getUserName().hashCode() : 0);
        result = 31 * result + (getMappingId() != null ? getMappingId().hashCode() : 0);
        result = 31 * result + (int) (getDate() ^ (getDate() >>> 32));
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uId);
        parcel.writeString(storeId);
        parcel.writeString(phoneNumber);
        parcel.writeLong(tokenNumber);
        parcel.writeLong(timestamp);
        parcel.writeInt(status);
        parcel.writeInt(buzzCount);
        parcel.writeString(senderPic);
        parcel.writeString(senderName);
        parcel.writeString(areaName);
        parcel.writeLong(activatedTokenTime);
        parcel.writeLong(tokenFinishTime);
        parcel.writeString(userName);
        parcel.writeLong(date);
        parcel.writeString(serviceId);
        parcel.writeLong(appointmentTime);
        parcel.writeString(timeRange);
    }

    protected Token(Parcel in) {
        uId = in.readString();
        storeId = in.readString();
        phoneNumber = in.readString();
        tokenNumber = in.readLong();
        timestamp = in.readLong();
        status = in.readInt();
        buzzCount = in.readInt();
        senderPic = in.readString();
        senderName = in.readString();
        areaName = in.readString();
        activatedTokenTime = in.readLong();
        tokenFinishTime = in.readLong();
        userName = in.readString();
        date = in.readLong();
        serviceId = in.readString();
        appointmentTime = in.readLong();
        timeRange = in.readString();
    }

    public static final Creator<Token> CREATOR = new Creator<Token>() {
        @Override
        public Token createFromParcel(Parcel in) {
            return new Token(in);
        }

        @Override
        public Token[] newArray(int size) {
            return new Token[size];
        }
    };
}
