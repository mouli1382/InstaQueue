package in.mobifirst.tagtree.services;

import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

@Module
public class ServicesPresenterModule {

    private final ServicesContract.View mView;

    private String mStoreId;

    public ServicesPresenterModule(ServicesContract.View view, @NonNull String storeId) {
        mView = view;
        mStoreId = storeId;
    }

    @Provides
    ServicesContract.View provideServicesContractView() {
        return mView;
    }

    @Provides
    @NonNull
    String provideStoreId() {
        return mStoreId;
    }
}
