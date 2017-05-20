package in.mobifirst.tagtree.tokens;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import dagger.Module;
import dagger.Provides;
import in.mobifirst.tagtree.model.Service;

@Module
public class TokensPresenterModule {

    private final TokensContract.View mView;
    private long mDate;
    private Service mService;

    public TokensPresenterModule(TokensContract.View view, @NonNull Service service, @Nullable long date) {
        mView = view;
        mService = service;
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

    @Provides
    @NonNull
    Service provideService() {
        return mService;
    }

}
