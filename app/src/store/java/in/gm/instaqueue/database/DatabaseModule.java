package in.gm.instaqueue.database;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.gm.instaqueue.authentication.FirebaseAuthenticationManager;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    public FirebaseDatabaseManager provideFirebaseDatabaseManager() {
        return new FirebaseDatabaseManager();
    }
}