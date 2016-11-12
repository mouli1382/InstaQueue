package in.mobifirst.tagtree.model;

public class Store {

    private String storeId;
    private String name;
    private String logoUrl;
    private long credits;

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

    public void setName(long credits) {
        this.credits = credits;
    }

}
