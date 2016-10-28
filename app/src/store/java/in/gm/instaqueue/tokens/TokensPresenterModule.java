package in.gm.instaqueue.tokens;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the View dependency to the
 * {@link TasksPresenter}.
 */
@Module
public class TokensPresenterModule {

    private final TasksContract.View mView;

    public TokensPresenterModule(TasksContract.View view) {
        mView = view;
    }

    @Provides
    TasksContract.View provideTasksContractView() {
        return mView;
    }

}
