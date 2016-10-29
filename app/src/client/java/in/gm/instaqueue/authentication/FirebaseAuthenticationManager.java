package in.gm.instaqueue.authentication;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthenticationManager implements AuthenticationManager {

    private FirebaseAuth mAuth;

    public FirebaseAuthenticationManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getAuthInstance() {
        return mAuth;
    }
}

