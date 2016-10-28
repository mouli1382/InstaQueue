package in.gm.instaqueue.tokens;

import android.os.Bundle;

import javax.inject.Inject;

import in.gm.instaqueue.R;
import in.gm.instaqueue.activity.BaseDrawerActivity;
import in.gm.instaqueue.util.ActivityUtilities;

public class TokensActivity extends BaseDrawerActivity {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    @Inject
    TokensPresenter mTokensPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TokensFragment tokensFragment =
                (TokensFragment) getSupportFragmentManager().findFragmentById(R.id.content_base_drawer);
        if (tokensFragment == null) {
            tokensFragment = TokensFragment.newInstance();
            ActivityUtilities.addFragmentToActivity(
                    getSupportFragmentManager(), tokensFragment, R.id.content_base_drawer);
        }

        // Create the presenter
        DaggerTokensComponent.builder()
                .appComponent(getApplication().get())
                .tokensPresenterModule(new TokensPresenterModule(tokensFragment)).build()
                .inject(this);

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
            TokensFilterType currentFiltering =
                    (TokensFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            mTokensPresenter.setFiltering(currentFiltering);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTERING_KEY, mTokensPresenter.getFiltering());

        super.onSaveInstanceState(outState);
    }
}
