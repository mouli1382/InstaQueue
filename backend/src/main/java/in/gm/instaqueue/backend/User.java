package in.mobifirst.tagtree.backend;

public class User {
    private String uId;
    private String name;
    private String phoneNumber;
    private String email;
    private String regId;

    public User(String uId, String name, String phoneNumber, String email, String regId) {
        this.uId = uId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.regId = regId;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }
}
