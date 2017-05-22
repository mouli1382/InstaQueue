package in.mobifirst.tagtree.addedittoken;

import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import in.mobifirst.tagtree.model.Token;

@Module
public class AddEditTokenPresenterModule {

    private final AddEditTokenContract.View mView;

    private Token mToken;

    public AddEditTokenPresenterModule(AddEditTokenContract.View view, @NonNull Token token) {
        mView = view;
        mToken = token;
    }

    @Provides
    AddEditTokenContract.View provideAddEditTokenContractView() {
        return mView;
    }


    @Provides
    @NonNull
    Token provideToken() {
        return mToken;
    }
}
