package in.mobifirst.tagtree.application;

import javax.inject.Singleton;

import dagger.Component;
import in.mobifirst.tagtree.activity.WelcomeActivity;
import in.mobifirst.tagtree.authentication.AuthenticationModule;
import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.authentication.digits.DigitsSignInActivity;
import in.mobifirst.tagtree.data.token.TokensRepository;
import in.mobifirst.tagtree.data.token.TokensRepositoryModule;
import in.mobifirst.tagtree.database.DatabaseModule;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;

@Singleton
@Component(modules = {TokensRepositoryModule.class, ApplicationModule.class, AuthenticationModule.class, DatabaseModule.class})
public interface ApplicationComponent {

    void inject(WelcomeActivity welcomeActivity);

    void inject(DigitsSignInActivity digitsSignInActivity);

    TokensRepository getTokensRepository();

    IQSharedPreferences getIQSharedPreferences();

    FirebaseAuthenticationManager getFirebaseAuthenticationManager();
}
