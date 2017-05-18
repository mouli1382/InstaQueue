package in.mobifirst.tagtree.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Service {

    private String id;
    private String storeId;
    private String name;
    private String description;
    private int daysOfOperation;

    // key = day:dayMask ; value = slot object
    private Map<String, Slot> slots;
    private int duration; //time per serve in minutes.

    //ToDo - move previous day's metrics to history to improve performance.
    // Holds date wise operational metrics.
    private Map<String, ServiceDateWiseData> dateWise;

    public Service(String storeId, String name, String description, int daysOfOperation, int duration) {
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.daysOfOperation = daysOfOperation;
        this.duration = duration;
    }

    public Service(String id, String storeId, String name, String description, int daysOfOperation, Map<String, Slot> slots, int duration) {
        this.id = id;
        this.storeId = storeId;
        this.name = name;
        this.description = description;
        this.daysOfOperation = daysOfOperation;
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

    public List<Slot> getSlots() {
        if (slots != null && slots.size() > 0) {
            return new ArrayList<>(slots.values());
        }
        return new ArrayList<>();
    }

    public void setSlots(List<Slot> slots) {
        Map<String, Slot> map = new HashMap<>();
        if (slots != null && slots.size() > 0) {
            for (Slot slot : slots)
                map.put(slot.getDay() + ":" + slot.getDaysMask(), slot);
        }
        this.slots = map;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
