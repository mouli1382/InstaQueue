package in.gm.instaqueue.application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.gm.instaqueue.preferences.IQSharedPreferences;

@Module
public class ApplicationModule {

    private IQApplication application;

    public ApplicationModule(IQApplication app) {
        this.application = app;
    }

    @Provides
    @Singleton
    public IQApplication provideIQApplication() {
        return application;
    }

    @Provides
    @Singleton
    public IQSharedPreferences provideIQSharedPreferences() {
        return new IQSharedPreferences(application);
    }
}