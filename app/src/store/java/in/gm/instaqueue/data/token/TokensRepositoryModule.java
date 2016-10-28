package in.gm.instaqueue.data.token;

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
