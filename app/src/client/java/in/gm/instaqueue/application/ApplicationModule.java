package in.gm.instaqueue.application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.gm.instaqueue.preferences.IQSharedPreferences;

@Module
public class ApplicationModule {

    private IQClientApplication application;

    public ApplicationModule(IQClientApplication app) {
        this.application = app;
    }

    @Provides
    @Singleton
    public IQClientApplication provideIQApplication() {
        return application;
    }

    @Provides
    @Singleton
    public IQSharedPreferences provideIQSharedPreferences() {
        return new IQSharedPreferences(application);
    }
}