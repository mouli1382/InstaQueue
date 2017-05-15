package in.mobifirst.tagtree.application;

import in.mobifirst.tagtree.application.DaggerApplicationComponent;
import in.mobifirst.tagtree.database.DatabaseModule;
import in.mobifirst.tagtree.authentication.AuthenticationModule;
import in.mobifirst.tagtree.data.token.TokensRepositoryModule;
import in.mobifirst.tagtree.storage.StorageModule;
import io.flic.lib.FlicManager;

public class IQStoreApplication extends IQApplication {
    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        setupObjectGraph();

        FlicManager.setAppCredentials("5ba577b5-ad43-4c40-b90a-c4686304a246", "a7e2e585-8f17-4180-a737-860e822905c7", "TagTree");
    }

    private void setupObjectGraph() {
        if (applicationComponent == null) {
            applicationComponent = DaggerApplicationComponent
                    .builder()
                    .databaseModule(new DatabaseModule())
                    .authenticationModule(new AuthenticationModule())
                    .applicationModule(new ApplicationModule(this))
                    .tokensRepositoryModule(new TokensRepositoryModule())
                    .storageModule(new StorageModule())
                    .build();
        }
    }

    public ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
