package in.gm.instaqueue.authentication;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

public class FirebaseAuthenticationManager implements AuthenticationManager {

    private FirebaseAuth mAuth;

    @Inject
    public FirebaseAuthenticationManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getAuthInstance() {
        return mAuth;
    }
}

