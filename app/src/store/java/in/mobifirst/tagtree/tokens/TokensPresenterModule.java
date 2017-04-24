package in.mobifirst.tagtree.tokens;

import android.support.annotation.Nullable;

import dagger.Module;
import dagger.Provides;

@Module
public class TokensPresenterModule {

    private final TokensContract.View mView;
    private long mDate;


    public TokensPresenterModule(TokensContract.View view, @Nullable long date) {
        mView = view;
        mDate = date;
    }

    @Provides
    TokensContract.View provideTokensContractView() {
        return mView;
    }

    @Provides
    @Nullable
    long provideDate() {
        return mDate;
    }

}
