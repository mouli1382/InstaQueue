package in.mobifirst.tagtree.application;

import javax.inject.Singleton;

import dagger.Component;
import in.mobifirst.tagtree.activity.WelcomeActivity;
import in.mobifirst.tagtree.authentication.AuthenticationModule;
import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.authentication.digits.DigitsSignInActivity;
import in.mobifirst.tagtree.authentication.google.GoogleSignInActivity;
import in.mobifirst.tagtree.token.TokensRepository;
import in.mobifirst.tagtree.token.TokensRepositoryModule;
import in.mobifirst.tagtree.database.DatabaseModule;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.tokens.TokensFragment;

@Singleton
@Component(modules = {TokensRepositoryModule.class, ApplicationModule.class, AuthenticationModule.class, DatabaseModule.class})
public interface ApplicationComponent {

    void inject(WelcomeActivity welcomeActivity);

    void inject(DigitsSignInActivity digitsSignInActivity);

    void inject(GoogleSignInActivity googleSignInActivity);

    void inject(TokensFragment tokensFragment);

    TokensRepository getTokensRepository();

    IQSharedPreferences getIQSharedPreferences();

    FirebaseAuthenticationManager getFirebaseAuthenticationManager();
}
