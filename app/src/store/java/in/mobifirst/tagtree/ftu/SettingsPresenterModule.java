package in.mobifirst.tagtree.ftu;

import dagger.Module;
import dagger.Provides;

@Module
public class SettingsPresenterModule {

    private final SettingsContract.View mView;

    public SettingsPresenterModule(SettingsContract.View view) {
        mView = view;
    }

    @Provides
    SettingsContract.View provideSettingsContractView() {
        return mView;
    }

}
