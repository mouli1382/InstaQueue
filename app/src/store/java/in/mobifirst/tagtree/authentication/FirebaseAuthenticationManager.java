package in.mobifirst.tagtree.authentication;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;

import in.mobifirst.tagtree.authentication.AuthenticationManager;

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

