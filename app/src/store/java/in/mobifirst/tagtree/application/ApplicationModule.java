package in.mobifirst.tagtree.application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;

@Module
public class ApplicationModule {

    private IQStoreApplication application;

    public ApplicationModule(IQStoreApplication app) {
        this.application = app;
    }

    @Provides
    @Singleton
    public IQStoreApplication provideIQApplication() {
        return application;
    }

    @Provides
    @Singleton
    public IQSharedPreferences provideIQSharedPreferences() {
        return new IQSharedPreferences(application);
    }
}