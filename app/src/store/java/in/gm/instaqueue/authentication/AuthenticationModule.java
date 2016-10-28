package in.gm.instaqueue.authentication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.gm.instaqueue.application.IQApplication;
import in.gm.instaqueue.authentication.digits.DigitsAuth;
import in.gm.instaqueue.authentication.digits.DigitsAuthenticationManager;
import in.gm.instaqueue.authentication.google.FirebaseAuthenticationManager;
import in.gm.instaqueue.authentication.google.GAuth;

@Module
public class AuthenticationModule {

    @Provides
    @DigitsAuth
    public DigitsAuthenticationManager provideDigitsAuthenticationManager() {
        return new DigitsAuthenticationManager();
    }

    @Provides
    @GAuth
    public FirebaseAuthenticationManager provideFirebaseAuthenticationManager() {
        return new FirebaseAuthenticationManager();
    }
}