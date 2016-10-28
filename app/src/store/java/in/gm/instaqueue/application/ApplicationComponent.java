package in.gm.instaqueue.application;

import javax.inject.Singleton;

import dagger.Component;
import in.gm.instaqueue.activity.WelcomeActivity;
import in.gm.instaqueue.data.token.TokensRepository;
import in.gm.instaqueue.data.token.TokensRepositoryModule;
import in.gm.instaqueue.preferences.IQSharedPreferences;

@Singleton
@Component(modules = {TokensRepositoryModule.class, ApplicationModule.class})
public interface ApplicationComponent {

    void inject(WelcomeActivity welcomeActivity);

    IQSharedPreferences getIQSharedPreferences();

    TokensRepository getTokensRepository();
}
