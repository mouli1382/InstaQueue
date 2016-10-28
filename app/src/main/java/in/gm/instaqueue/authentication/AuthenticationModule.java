package in.gm.instaqueue.authentication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AuthenticationModule {

    @Provides
    @Singleton
    public AuthenticationManager provideAuthenticationManager() {
        return null;
    }
}