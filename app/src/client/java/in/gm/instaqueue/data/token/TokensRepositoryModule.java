package in.gm.instaqueue.data.token;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.gm.instaqueue.authentication.FirebaseAuthenticationManager;
import in.gm.instaqueue.database.FirebaseDatabaseManager;
import in.gm.instaqueue.preferences.IQSharedPreferences;

@Module
public class TokensRepositoryModule {

    private TokensDataSourceImpl mTokensDataSourceImpl;

    @Singleton
    @Provides
    TokensDataSource provideTokensDataSource(FirebaseDatabaseManager firebaseDatabaseManager, IQSharedPreferences iqSharedPreferences) {
        if (null == mTokensDataSourceImpl)
            mTokensDataSourceImpl = new TokensDataSourceImpl(firebaseDatabaseManager, iqSharedPreferences);
        return mTokensDataSourceImpl;
    }
}
