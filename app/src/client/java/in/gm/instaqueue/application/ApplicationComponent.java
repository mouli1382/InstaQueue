package in.gm.instaqueue.application;

import javax.inject.Singleton;

import dagger.Component;
import in.gm.instaqueue.activity.WelcomeActivity;
import in.gm.instaqueue.authentication.AuthenticationModule;
import in.gm.instaqueue.authentication.digits.DigitsSignInActivity;
import in.gm.instaqueue.database.DatabaseModule;
import in.gm.instaqueue.token.TokensRepository;
import in.gm.instaqueue.token.TokensRepositoryModule;

@Singleton
@Component(modules = {TokensRepositoryModule.class, ApplicationModule.class, AuthenticationModule.class, DatabaseModule.class})
public interface ApplicationComponent {

    void inject(WelcomeActivity welcomeActivity);

    void inject(DigitsSignInActivity digitsSignInActivity);

    TokensRepository getTokensRepository();
}
