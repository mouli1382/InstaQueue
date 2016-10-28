package in.gm.instaqueue.database;

import dagger.Module;
import dagger.Provides;

@Module
public class DatabaseModule {

    @Provides
    public FirebaseDatabaseManager provideDatabaseManager() {
        return new FirebaseDatabaseManager();
    }
}