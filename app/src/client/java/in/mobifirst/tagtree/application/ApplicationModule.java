package in.mobifirst.tagtree.application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.util.NetworkConnectionUtils;

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

    @Provides
    @Singleton
    NetworkConnectionUtils provideNetworkConnectionUtils() {
        return new NetworkConnectionUtils(application);
    }
}