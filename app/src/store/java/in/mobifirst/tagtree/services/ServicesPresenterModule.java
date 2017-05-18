package in.mobifirst.tagtree.services;

import android.support.annotation.Nullable;

import dagger.Module;
import dagger.Provides;

@Module
public class ServicesPresenterModule {

    private final ServicesContract.View mView;

    private String mStoreId;

    public ServicesPresenterModule(ServicesContract.View view, @Nullable String storeId) {
        mView = view;
        mStoreId = storeId;
    }

    @Provides
    ServicesContract.View provideTokensContractView() {
        return mView;
    }

    @Provides
    @Nullable
    String provideStoreId() {
        return mStoreId;
    }
}
