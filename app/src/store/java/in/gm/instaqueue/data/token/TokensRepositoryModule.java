package in.gm.instaqueue.data.token;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.gm.instaqueue.authentication.FirebaseAuthenticationManager;
import in.gm.instaqueue.database.FirebaseDatabaseManager;

@Module
public class TokensRepositoryModule {

    private TokensDataSourceImpl mTokensDataSourceImpl;

    @Singleton
    @Provides
    TokensDataSource provideTokensDataSource(FirebaseDatabaseManager firebaseDatabaseManager, FirebaseAuthenticationManager firebaseAuthenticationManager) {
        if (null == mTokensDataSourceImpl)
            mTokensDataSourceImpl = new TokensDataSourceImpl(firebaseDatabaseManager, firebaseAuthenticationManager);
        return mTokensDataSourceImpl;
    }
}
