package in.mobifirst.tagtree.model;

import android.text.TextUtils;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.util.ApplicationConstants;

public class Store {

    private String storeId;
    private String name;
    private String area;
    private String website;
    private String logoUrl;
    private long credits;
    private long tokenCounter;
    private long globaltokenCounter;
    private long smsCounter;

    private String businessType;
    private List<String> services;

    public Store(String storeId, String name, String area, String website, String logoUrl, int numberOfCounters, String businessType, List<String> services) {
        this.storeId = storeId;
        this.name = name;
        this.area = area;
        this.website = website;
        this.logoUrl = logoUrl;
        this.businessType = businessType;
        this.services = services;
    }

    public Store(String name, String area, String website, String logoUrl, String businessType) {
        this.name = name;
        this.area = area;
        this.website = website;
        this.logoUrl = logoUrl;
        this.businessType = businessType;
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

    public long getSmsCounter() {
        return smsCounter;
    }

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

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("storeId", storeId);
        result.put("name", name);
        result.put("area", area);
        result.put("website", website);
        result.put("logoUrl", logoUrl);
        result.put("businessType", businessType);
        return result;
    }

    @Exclude
    public void persistStore(IQSharedPreferences sharedPreferences) {
        sharedPreferences.putString(ApplicationConstants.DISPLAY_NAME_KEY, name);
        sharedPreferences.putString(ApplicationConstants.AREA_NAME_KEY, area);
        sharedPreferences.putString(ApplicationConstants.WEBSITE_KEY, website);
        sharedPreferences.putString(ApplicationConstants.WEBSITE_LOGO_URL_KEY, logoUrl);
//        sharedPreferences.putLong(ApplicationConstants.CREDITS_KEY, credits);
    }

    @Exclude
    public static void clearStore(IQSharedPreferences sharedPreferences) {
        sharedPreferences.remove(ApplicationConstants.DISPLAY_NAME_KEY);
        sharedPreferences.remove(ApplicationConstants.AREA_NAME_KEY);
        sharedPreferences.remove(ApplicationConstants.WEBSITE_KEY);
        sharedPreferences.remove(ApplicationConstants.WEBSITE_LOGO_URL_KEY);
//        sharedPreferences.putLong(ApplicationConstants.CREDITS_KEY, credits);
    }
}
