package in.mobifirst.tagtree.model;

import android.text.TextUtils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.util.ApplicationConstants;

public class Store {

    private String storeId;
    private String name;
    private String area;
    private String website;
    private String logoUrl;
    private int numberOfCounters;
    private long credits;
    private long tokenCounter;
    private long globaltokenCounter;
    private long smsCounter;

    public Store(String name, String area, String website, String logoUrl, int numberOfCounters) {
        this.name = name;
        this.area = area;
        this.website = website;
        this.logoUrl = logoUrl;
        this.numberOfCounters = numberOfCounters;
    }

    public Store() {
        // Default constructor required for calls to DataSnapshot.getValue(Token.class)
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

    public long getTokenCounter() {
        return tokenCounter;
    }

    public long getSmsCounter() { return smsCounter; }

    public long getCredits() {
        return credits;
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
        return result;
    }

    @Exclude
    public void persistStore(IQSharedPreferences sharedPreferences) {
        sharedPreferences.putString(ApplicationConstants.DISPLAY_NAME_KEY, name);
        sharedPreferences.putString(ApplicationConstants.AREA_NAME_KEY, area);
        sharedPreferences.putString(ApplicationConstants.WEBSITE_KEY, website);
        sharedPreferences.putString(ApplicationConstants.WEBSITE_LOGO_URL_KEY, logoUrl);
        sharedPreferences.putInt(ApplicationConstants.NUMBER_OF_COUNTERS_KEY, numberOfCounters);
//        sharedPreferences.putLong(ApplicationConstants.CREDITS_KEY, credits);
    }

    @Exclude
    public static void clearStore(IQSharedPreferences sharedPreferences) {
        sharedPreferences.remove(ApplicationConstants.DISPLAY_NAME_KEY);
        sharedPreferences.remove(ApplicationConstants.AREA_NAME_KEY);
        sharedPreferences.remove(ApplicationConstants.WEBSITE_KEY);
        sharedPreferences.remove(ApplicationConstants.WEBSITE_LOGO_URL_KEY);
        sharedPreferences.remove(ApplicationConstants.NUMBER_OF_COUNTERS_KEY);
//        sharedPreferences.putLong(ApplicationConstants.CREDITS_KEY, credits);
    }
}
