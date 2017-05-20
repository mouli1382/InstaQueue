package in.mobifirst.tagtree.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service implements Parcelable {

    private String id;
    private String storeId;
    private String name;
    private String description;
    private int daysOfOperation;

    // key = day:dayMask ; value = slot object
    private List<Slot> slots;
    private int duration; //time per serve in minutes.

    //ToDo - move previous day's metrics to history to improve performance.
    // Holds date wise operational metrics.
    private Map<String, ServiceDateWiseData> dateWise;

    public Service() {
    }

    public Service(String storeId, String name, String description, int daysOfOperation, int duration) {
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.daysOfOperation = daysOfOperation;
        this.duration = duration;
    }

    public Service(String id, String storeId, String name, String description, List<Slot> slots, int duration) {
        this.id = id;
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.slots = slots;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDaysOfOperation() {
        return daysOfOperation;
    }

    public void setDaysOfOperation(int daysOfOperation) {
        this.daysOfOperation = daysOfOperation;
    }

    @Exclude
    public Map<Integer, Slot> getSlotsMap() {
        Map<Integer, Slot> map = new HashMap<>();
        if (slots != null && slots.size() > 0) {
            for (Slot slot : slots)
                map.put(slot.getDaysMask(), slot);
        }
        return map;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Exclude
    public boolean isEmpty() {
        return TextUtils.isEmpty(name)
                || TextUtils.isEmpty(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(storeId);
        parcel.writeString(name);
        parcel.writeString(description);
    }

    protected Service(Parcel in) {
        id = in.readString();
        storeId = in.readString();
        name = in.readString();
        description = in.readString();
    }

    public static final Creator<Service> CREATOR = new Creator<Service>() {
        @Override
        public Service createFromParcel(Parcel in) {
            return new Service(in);
        }

        @Override
        public Service[] newArray(int size) {
            return new Service[size];
        }
    };
}
