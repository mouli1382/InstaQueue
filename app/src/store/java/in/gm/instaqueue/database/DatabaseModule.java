package in.gm.instaqueue.database;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.gm.instaqueue.authentication.FirebaseAuthenticationManager;

@Module
public class DatabaseModule {

    private Context mContext;
    public DatabaseModule(Context context)
    {
        mContext = context;
    }
    @Provides
    @Singleton
    public FirebaseDatabaseManager provideFirebaseDatabaseManager() {
        return new FirebaseDatabaseManager(mContext);
    }
}