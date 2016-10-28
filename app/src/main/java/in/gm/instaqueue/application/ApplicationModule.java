package in.gm.instaqueue.application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private IQApplication application;

    public ApplicationModule(IQApplication app) {
        this.application = app;
    }

    @Provides
    @Singleton
    public IQApplication provideApplication() {
        return application;
    }
}