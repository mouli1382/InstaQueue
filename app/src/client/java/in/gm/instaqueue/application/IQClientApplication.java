package in.mobifirst.tagtree.application;

import in.mobifirst.tagtree.authentication.AuthenticationModule;
import in.mobifirst.tagtree.data.token.TokensRepositoryModule;
import in.mobifirst.tagtree.database.DatabaseModule;

public class IQClientApplication extends IQApplication {
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
