package in.gm.instaqueue.dagger.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.gm.instaqueue.firebase.FirebaseManager;

@Module
public class DbModule {

    @Provides
    @Singleton
    public FirebaseManager provideFirebaseManager() {
        return new FirebaseManager();
    }
}