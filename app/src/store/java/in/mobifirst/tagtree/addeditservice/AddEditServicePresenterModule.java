package in.mobifirst.tagtree.addeditservice;

import dagger.Module;
import dagger.Provides;

@Module
public class AddEditServicePresenterModule {

    private final AddEditServiceContract.View mView;

    public AddEditServicePresenterModule(AddEditServiceContract.View view) {
        mView = view;
    }

    @Provides
    AddEditServiceContract.View provideSettingsContractView() {
        return mView;
    }

}
