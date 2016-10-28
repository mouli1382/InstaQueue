package in.gm.instaqueue.application;

import android.app.Application;

import in.gm.instaqueue.data.token.DaggerTokensRepositoryComponent;
import in.gm.instaqueue.data.token.TokensRepositoryComponent;
import in.gm.instaqueue.data.token.TokensRepositoryModule;

public class IQApplication extends Application {
    private TokensRepositoryComponent tokensRepositoryComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        setupObjectGraph();
    }

    private void setupObjectGraph() {
        tokensRepositoryComponent = DaggerTokensRepositoryComponent
                .builder()
                .tokensRepositoryModule(new TokensRepositoryModule())
                .build();
    }

    public TokensRepositoryComponent getTokensRepositoryComponent() {
        return tokensRepositoryComponent;
    }
}
