package in.mobifirst.tagtree.addeditservice;

import android.support.annotation.Nullable;

import dagger.Module;
import dagger.Provides;

@Module
public class AddEditServicePresenterModule {

    private final AddEditServiceContract.View mView;

    private String mServiceId;

    public AddEditServicePresenterModule(AddEditServiceContract.View view, @Nullable String serviceId) {
        mView = view;
        mServiceId = serviceId;
    }

    @Provides
    AddEditServiceContract.View provideSettingsContractView() {
        return mView;
    }

    @Provides
    @Nullable
    String provideServiceId() {
        return mServiceId;
    }

}
