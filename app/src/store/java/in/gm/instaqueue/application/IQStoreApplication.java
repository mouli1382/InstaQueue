package in.gm.instaqueue.application;

import in.gm.instaqueue.authentication.AuthenticationModule;
import in.gm.instaqueue.data.token.TokensRepositoryModule;
import in.gm.instaqueue.database.DatabaseModule;

public class IQStoreApplication extends IQApplication {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        setupObjectGraph();
    }

    private void setupObjectGraph() {
        if (applicationComponent == null) {
            applicationComponent = DaggerApplicationComponent
                    .builder()
                    .databaseModule(new DatabaseModule())
                    .authenticationModule(new AuthenticationModule())
                    .applicationModule(new ApplicationModule(this))
                    .tokensRepositoryModule(new TokensRepositoryModule())
                    .build();
        }
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
