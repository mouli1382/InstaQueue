package in.gm.instaqueue.application;

import javax.inject.Singleton;

import dagger.Component;

import in.gm.instaqueue.activity.RazorPayActivity;
import in.gm.instaqueue.activity.StoreOnboarding;

import in.gm.instaqueue.activity.WelcomeActivity;
import in.gm.instaqueue.authentication.AuthenticationModule;
import in.gm.instaqueue.authentication.FirebaseAuthenticationManager;
import in.gm.instaqueue.authentication.digits.DigitsSignInActivity;
import in.gm.instaqueue.authentication.google.GoogleSignInActivity;
import in.gm.instaqueue.data.token.TokensRepository;
import in.gm.instaqueue.data.token.TokensRepositoryModule;
import in.gm.instaqueue.database.DatabaseModule;

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
