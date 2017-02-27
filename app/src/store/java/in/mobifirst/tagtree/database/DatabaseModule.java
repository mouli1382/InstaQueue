package in.mobifirst.tagtree.database;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;

@Module
public class DatabaseModule {

    @Provides
    @Singleton
    public FirebaseDatabaseManager provideFirebaseDatabaseManager(IQStoreApplication application, IQSharedPreferences iqSharedPreferences) {
        return new FirebaseDatabaseManager(application, iqSharedPreferences);
    }
}