package in.gm.instaqueue.dagger.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.gm.instaqueue.app.IQApplication;

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