package in.gm.instaqueue.dagger.module;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.gm.instaqueue.prefs.SharedPrefs;

@Module
public class PreferenceModule {

    @Provides
    @Singleton
    public SharedPrefs provideSharedPrefs(Application application) {
        return new SharedPrefs(application);
    }
}