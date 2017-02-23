package in.mobifirst.tagtree.backend.model;

/**
 * The object model for the data we are sending through endpoints
 */
public class ApiResponse {

    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}