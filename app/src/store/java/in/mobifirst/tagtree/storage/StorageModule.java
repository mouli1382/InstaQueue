package in.mobifirst.tagtree.storage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;

@Module
public class StorageModule {

    @Provides
    @Singleton
    public FirebaseStorageManager provideFirebaseStorageManager(IQSharedPreferences iqSharedPreferences) {
        return new FirebaseStorageManager(iqSharedPreferences);
    }
}