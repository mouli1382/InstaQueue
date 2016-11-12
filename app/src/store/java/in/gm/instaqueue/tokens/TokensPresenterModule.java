package in.mobifirst.tagtree.tokens;

import dagger.Module;
import dagger.Provides;

@Module
public class TokensPresenterModule {

    private final TokensContract.View mView;

    public TokensPresenterModule(TokensContract.View view) {
        mView = view;
    }

    @Provides
    TokensContract.View provideTokensContractView() {
        return mView;
    }

}
