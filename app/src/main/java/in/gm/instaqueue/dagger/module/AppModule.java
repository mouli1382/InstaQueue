package in.gm.instaqueue.dagger.module;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.gm.instaqueue.app.IQApplication;

@Module
public class AppModule {

    private IQApplication application;

    public AppModule(IQApplication app) {
        this.application = app;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return application;
    }
}