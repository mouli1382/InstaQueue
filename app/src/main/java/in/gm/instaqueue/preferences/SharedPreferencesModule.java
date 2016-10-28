package in.gm.instaqueue.preferences;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SharedPreferencesModule {

    @Provides
    @Singleton
    public IQSharedPreferences provideSharedPrefs(Application application) {
        return new IQSharedPreferences(application);
    }
}