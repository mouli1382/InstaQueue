package in.gm.instaqueue.authentication.digits;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import in.gm.instaqueue.authentication.AuthenticationManager;

public class DigitsAuthenticationManager implements AuthenticationManager {

    private FirebaseAuth mAuth;

    @Inject
    public DigitsAuthenticationManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getAuthInstance() {
        return mAuth;
    }
}

