package in.mobifirst.tagtree.application;

import javax.inject.Singleton;

import dagger.Component;
import in.mobifirst.tagtree.activity.BaseDrawerActivity;
import in.mobifirst.tagtree.activity.RazorPayActivity;
import in.mobifirst.tagtree.activity.WelcomeActivity;
import in.mobifirst.tagtree.addeditservice.AddEditServiceFragment;
import in.mobifirst.tagtree.addeditservice.ServiceDetailsFetcherActivity;
import in.mobifirst.tagtree.addedittoken.AddEditTokenFragment;
import in.mobifirst.tagtree.authentication.AuthenticationModule;
import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.authentication.google.GoogleSignInActivity;
import in.mobifirst.tagtree.config.ResetPreference;
import in.mobifirst.tagtree.data.token.TokensRepository;
import in.mobifirst.tagtree.data.token.TokensRepositoryModule;
import in.mobifirst.tagtree.database.DatabaseModule;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.addeditstore.StoreDetailsFetcherActivity;
import in.mobifirst.tagtree.addeditstore.AddEditStoreFragment;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.services.ServicesFragment;
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

    void inject(StoreDetailsFetcherActivity settingsFetcherActivity);

    void inject(ServiceDetailsFetcherActivity serviceDetailsFetcherActivity);

    void inject(AddEditStoreFragment settingsFragment);

    void inject(ServicesFragment servicesFragment);

    void inject(AddEditServiceFragment serviceFragment);

    void inject(TokensFragment tokensFragment);

    void inject(AddEditTokenFragment addEditTokenFragment);

    void inject(SnapFragment snapFragment);

    void inject(BaseDrawerActivity baseDrawerActivity);

    void inject(SmsReceiver smsReceiver);

    void inject(ResetPreference resetPreference);

    TokensRepository getTokensRepository();

    FirebaseAuthenticationManager getFirebaseAuthenticationManager();

    FirebaseDatabaseManager getFirebaseDatabaseManager();

    FirebaseStorageManager getFirebaseStorageManager();

    IQSharedPreferences getIQSharedPreferences();
}
