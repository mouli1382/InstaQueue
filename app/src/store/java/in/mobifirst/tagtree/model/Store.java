package in.mobifirst.tagtree.model;

import android.text.TextUtils;

public class Store {

    private String storeId;
    private String name;
    private String area;
    private String website;
    private String logoUrl;
    private long credits;

    public Store(String name, String area, String website, String logoUrl, long credits) {
        this.name = name;
        this.area = area;
        this.website = website;
        this.logoUrl = logoUrl;
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
}
