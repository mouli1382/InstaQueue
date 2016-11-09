package in.gm.instaqueue.database;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.gm.instaqueue.preferences.IQSharedPreferences;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    public FirebaseDatabaseManager provideFirebaseDatabaseManager(IQSharedPreferences iqSharedPreferences) {
        return new FirebaseDatabaseManager(iqSharedPreferences);
    }
}