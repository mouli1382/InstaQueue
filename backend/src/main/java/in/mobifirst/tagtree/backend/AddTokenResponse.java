package in.mobifirst.tagtree.backend;

import java.util.concurrent.Callable;

/**
 * The object model for the data we are sending through endpoints
 */
public class AddTokenResponse {

    private boolean status;

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean data) {
        status = data;
    }
}