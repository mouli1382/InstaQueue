package in.gm.instaqueue.application;

import android.app.Application;

import in.gm.instaqueue.data.token.TokensRepositoryModule;

public class IQApplication extends Application {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        setupObjectGraph();
    }

    private void setupObjectGraph() {
        applicationComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .tokensRepositoryModule(new TokensRepositoryModule())
                .build();
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
