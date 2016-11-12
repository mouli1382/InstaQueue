package in.mobifirst.tagtree.authentication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AuthenticationModule {
    private FirebaseAuthenticationManager firebaseAuthenticationManager;

    @Provides
    @Singleton
    public FirebaseAuthenticationManager provideGoogleAuthenticationManager() {
        if (null == firebaseAuthenticationManager) {
            firebaseAuthenticationManager = new FirebaseAuthenticationManager();
        }
        return firebaseAuthenticationManager;
    }
}