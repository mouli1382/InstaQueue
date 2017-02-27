package in.mobifirst.tagtree.addedittoken;

import android.support.annotation.Nullable;

import dagger.Module;
import dagger.Provides;

@Module
public class AddEditTokenPresenterModule {

    private final AddEditTokenContract.View mView;

    private String mTokenId;

    public AddEditTokenPresenterModule(AddEditTokenContract.View view, @Nullable String tokenId) {
        mView = view;
        mTokenId = tokenId;
    }

    @Provides
    AddEditTokenContract.View provideAddEditTokenContractView() {
        return mView;
    }

    @Provides
    @Nullable
    String provideTokenId() {
        return mTokenId;
    }
}
