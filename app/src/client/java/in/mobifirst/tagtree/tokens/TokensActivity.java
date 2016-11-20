package in.mobifirst.tagtree.tokens;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.BaseDrawerActivity;
import in.mobifirst.tagtree.application.IQClientApplication;
import in.mobifirst.tagtree.util.ActivityUtilities;
import in.mobifirst.tagtree.util.ApplicationConstants;

public class TokensActivity extends BaseDrawerActivity {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    @Inject
    TokensPresenter mTokensPresenter;

    public static void start(Context caller) {
        Intent intent = new Intent(caller, TokensActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TokensFragment tokensFragment =
                (TokensFragment) getSupportFragmentManager().findFragmentById(R.id.content_base_drawer);
        if (tokensFragment == null) {
            tokensFragment = TokensFragment.newInstance();
            tokensFragment.setArguments(getIntent().getBundleExtra(ApplicationConstants.BUNDLE_KEY));
            ActivityUtilities.addFragmentToActivity(
                    getSupportFragmentManager(), tokensFragment, R.id.content_base_drawer);
        }

        // Create the presenter
        DaggerTokensComponent.builder()
                .applicationComponent(((IQClientApplication) getApplication()).getApplicationComponent())
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
