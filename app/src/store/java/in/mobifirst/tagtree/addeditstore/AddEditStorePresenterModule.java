package in.mobifirst.tagtree.addeditstore;

import dagger.Module;
import dagger.Provides;

@Module
public class AddEditStorePresenterModule {

    private final AddEditStoreContract.View mView;

    public AddEditStorePresenterModule(AddEditStoreContract.View view) {
        mView = view;
    }

    @Provides
    AddEditStoreContract.View provideSettingsContractView() {
        return mView;
    }

}
