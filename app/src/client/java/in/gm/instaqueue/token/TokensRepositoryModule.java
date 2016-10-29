package in.gm.instaqueue.token;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TokensRepositoryModule {

    @Singleton
    @Provides
    TokensDataSource provideTokensDataSource() {
        return null;
    }
}
