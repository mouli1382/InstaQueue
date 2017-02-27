package io.swagger.api.impl.model;

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

    public static ApiResponse successResponse() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(0);
        apiResponse.setMessage("Successfully called the next person in line.");

        return apiResponse;
    }

    public static ApiResponse errorResponse() {
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(-1);
        apiResponse.setMessage("Failed to call the next person in line.");

        return apiResponse;
    }
}