package in.mobifirst.tagtree.token;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;

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
