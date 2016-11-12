package in.mobifirst.tagtree.authentication;

import com.digits.sdk.android.Digits;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.mobifirst.tagtree.BuildConfig;
import in.mobifirst.tagtree.application.IQStoreApplication;
import io.fabric.sdk.android.Fabric;

@Module
public class AuthenticationModule {
    private FirebaseAuthenticationManager firebaseAuthenticationManager;

    @Provides
    @Named("digits")
    @Singleton
    public FirebaseAuthenticationManager provideDigitsAuthenticationManager(IQStoreApplication application) {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(BuildConfig.TWITTER_KEY, BuildConfig.TWITTER_SECRET);
        Fabric.with(application, new TwitterCore(authConfig), new Digits.Builder().build());
        if (null == firebaseAuthenticationManager) {
            firebaseAuthenticationManager = new FirebaseAuthenticationManager();
        }
        return firebaseAuthenticationManager;
    }

    @Provides
    @Named("gauth")
    @Singleton
    public FirebaseAuthenticationManager provideGoogleAuthenticationManager() {
        if (null == firebaseAuthenticationManager) {
            firebaseAuthenticationManager = new FirebaseAuthenticationManager();
        }
        return firebaseAuthenticationManager;
    }
}