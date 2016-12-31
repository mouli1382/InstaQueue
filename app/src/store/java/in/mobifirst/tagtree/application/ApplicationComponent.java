package in.mobifirst.tagtree.application;

import javax.inject.Singleton;

import dagger.Component;
import in.mobifirst.tagtree.activity.BaseDrawerActivity;
import in.mobifirst.tagtree.activity.RazorPayActivity;
import in.mobifirst.tagtree.activity.WelcomeActivity;
import in.mobifirst.tagtree.addedittoken.AddEditTokenFragment;
import in.mobifirst.tagtree.authentication.AuthenticationModule;
import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.authentication.google.GoogleSignInActivity;
import in.mobifirst.tagtree.data.token.TokensRepository;
import in.mobifirst.tagtree.data.token.TokensRepositoryModule;
import in.mobifirst.tagtree.database.DatabaseModule;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.ftu.SettingsActivity;
import in.mobifirst.tagtree.ftu.SettingsFetcherActivity;
import in.mobifirst.tagtree.ftu.SettingsFragment;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.sms.SmsReceiver;
import in.mobifirst.tagtree.storage.FirebaseStorageManager;
import in.mobifirst.tagtree.storage.StorageModule;
import in.mobifirst.tagtree.tokens.SnapFragment;
import in.mobifirst.tagtree.tokens.TokensFragment;

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

    void inject(SettingsFragment settingsFragment);

    void inject(TokensFragment tokensFragment);

    void inject(AddEditTokenFragment addEditTokenFragment);

    void inject(SnapFragment snapFragment);

    void inject(BaseDrawerActivity baseDrawerActivity);

    void inject(SmsReceiver smsReceiver);

    TokensRepository getTokensRepository();

    FirebaseAuthenticationManager getFirebaseAuthenticationManager();

    FirebaseDatabaseManager getFirebaseDatabaseManager();

    FirebaseStorageManager getFirebaseStorageManager();

    IQSharedPreferences getIQSharedPreferences();
}
