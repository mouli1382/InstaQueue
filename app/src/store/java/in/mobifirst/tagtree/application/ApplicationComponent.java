package in.mobifirst.tagtree.application;

import javax.inject.Singleton;

import dagger.Component;

import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.authentication.google.GoogleSignInActivity;
import in.mobifirst.tagtree.database.DatabaseModule;
import in.mobifirst.tagtree.activity.RazorPayActivity;

import in.mobifirst.tagtree.activity.WelcomeActivity;
import in.mobifirst.tagtree.authentication.AuthenticationModule;
import in.mobifirst.tagtree.data.token.TokensRepository;
import in.mobifirst.tagtree.data.token.TokensRepositoryModule;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.ftu.SettingsFetcherActivity;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.storage.FirebaseStorageManager;
import in.mobifirst.tagtree.storage.StorageModule;

@Singleton
@Component(modules = {
        TokensRepositoryModule.class,
        ApplicationModule.class,
        AuthenticationModule.class,
        DatabaseModule.class,
        StorageModule.class
})
public interface ApplicationComponent {

    void inject(WelcomeActivity welcomeActivity);

    void inject(RazorPayActivity razorPayActivity);

    void inject(GoogleSignInActivity googleSignInActivity);

    void inject(SettingsFetcherActivity settingsFetcherActivity);

    TokensRepository getTokensRepository();

    FirebaseAuthenticationManager getFirebaseAuthenticationManager();

    FirebaseDatabaseManager getFirebaseDatabaseManager();

    FirebaseStorageManager getFirebaseStorageManager();

    IQSharedPreferences getIQSharedPreferences();
}
