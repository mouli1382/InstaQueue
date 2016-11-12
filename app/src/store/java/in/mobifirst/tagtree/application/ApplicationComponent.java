package in.mobifirst.tagtree.application;

import javax.inject.Singleton;

import dagger.Component;

import in.mobifirst.tagtree.authentication.google.GoogleSignInActivity;
import in.mobifirst.tagtree.database.DatabaseModule;
import in.mobifirst.tagtree.activity.RazorPayActivity;
import in.mobifirst.tagtree.activity.StoreOnboarding;

import in.mobifirst.tagtree.activity.WelcomeActivity;
import in.mobifirst.tagtree.authentication.AuthenticationModule;
import in.mobifirst.tagtree.authentication.digits.DigitsSignInActivity;
import in.mobifirst.tagtree.data.token.TokensRepository;
import in.mobifirst.tagtree.data.token.TokensRepositoryModule;

@Singleton
@Component(modules = {TokensRepositoryModule.class, ApplicationModule.class, AuthenticationModule.class, DatabaseModule.class})
public interface ApplicationComponent {

    void inject(WelcomeActivity welcomeActivity);

    void inject(RazorPayActivity razorPayActivity);

    void inject(DigitsSignInActivity digitsSignInActivity);

    void inject(GoogleSignInActivity googleSignInActivity);

    void inject(StoreOnboarding storeOnboarding);

    TokensRepository getTokensRepository();
}
