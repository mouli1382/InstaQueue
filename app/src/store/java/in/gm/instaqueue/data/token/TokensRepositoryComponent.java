package in.gm.instaqueue.data.token;

import javax.inject.Singleton;

import dagger.Component;
import in.gm.instaqueue.application.ApplicationModule;

@Singleton
@Component(modules = {TokensRepositoryModule.class, ApplicationModule.class})
public interface TokensRepositoryComponent {

    TokensRepository getTokensRepository();
}
