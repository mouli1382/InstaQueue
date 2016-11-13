package in.mobifirst.tagtree.model;

import android.text.TextUtils;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;
import java.util.Map;

public class Store {

    private String storeId;
    private String name;
    private String area;
    private String website;
    private String logoUrl;
    private int numberOfCounters;
    private long credits;

    public Store(String name, String area, String website, String logoUrl, int numberOfCounters, long credits) {
        this.name = name;
        this.area = area;
        this.website = website;
        this.logoUrl = logoUrl;
        this.numberOfCounters = numberOfCounters;
        this.credits = credits;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
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

    public long getCredits() {
        return credits;
    }

    public void setCredits(long credits) {
        this.credits = credits;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public boolean isEmpty() {
        return TextUtils.isEmpty(name)
                || TextUtils.isEmpty(logoUrl);
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public int getNumberOfCounters() {
        return numberOfCounters;
    }

    public void setNumberOfCounters(int numberOfCounters) {
        this.numberOfCounters = numberOfCounters;
    }
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("storeId", storeId);
        result.put("name", name);
        result.put("area", area);
        result.put("website", website);
        result.put("logoUrl", logoUrl);
        result.put("numberOfCounters", numberOfCounters);
        result.put("credits", credits);
        return result;
    }
}
