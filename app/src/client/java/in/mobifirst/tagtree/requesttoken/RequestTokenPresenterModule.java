package in.mobifirst.tagtree.requesttoken;

import dagger.Module;
import dagger.Provides;

@Module
public class RequestTokenPresenterModule {

    private final RequestTokenContract.View mView;

    public RequestTokenPresenterModule(RequestTokenContract.View view) {
        mView = view;
    }

    @Provides
    RequestTokenContract.View provideRequestTokenContractView() {
        return mView;
    }
}
