package in.gm.instaqueue.database;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    public DatabaseManager provideDatabaseManager() {
        return null;
    }
}