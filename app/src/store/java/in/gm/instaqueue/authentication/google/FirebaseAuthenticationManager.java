package in.gm.instaqueue.authentication.google;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Singleton;

import in.gm.instaqueue.authentication.AuthenticationManager;

public class FirebaseAuthenticationManager implements AuthenticationManager {

    private FirebaseAuth mAuth;

    public FirebaseAuthenticationManager() {
        mAuth = FirebaseAuth.getInstance();
    }

    public FirebaseAuth getAuthInstance() {
        return mAuth;
    }
}

