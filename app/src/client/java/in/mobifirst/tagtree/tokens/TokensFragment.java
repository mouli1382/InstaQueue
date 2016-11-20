package in.mobifirst.tagtree.tokens;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.application.IQClientApplication;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.util.NotificationUtil;
import in.mobifirst.tagtree.util.SoundUtil;

public class TokensFragment extends Fragment implements TokensContract.View {

    private TokensContract.Presenter mPresenter;

    private TokensAdapter mTokensAdapter;

    private View mNoTokensView;

    private ImageView mNoTokenIcon;

    private TextView mNoTokenMainView;

    private LinearLayout mTokensView;

    private TextView mFilteringLabelView;

    public TokensFragment() {
        // Requires empty public constructor
    }

    public static TokensFragment newInstance() {
        return new TokensFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((IQClientApplication) getActivity().getApplicationContext()).getApplicationComponent()
                .inject(this);
        mTokensAdapter = new TokensAdapter(getActivity(), new ArrayList<Token>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(@NonNull TokensContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tokens, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.tokens_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mFilteringLabelView = (TextView) root.findViewById(R.id.filteringLabel);
        mTokensView = (LinearLayout) root.findViewById(R.id.tokensLL);

        // Set up  no Tokens view
        mNoTokensView = root.findViewById(R.id.notokens);
        mNoTokenIcon = (ImageView) root.findViewById(R.id.notokensIcon);
        mNoTokenMainView = (TextView) root.findViewById(R.id.notokensMain);

        // Set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadTokens(false);
            }
        });

        setLoadingIndicator(true);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mTokensAdapter);


        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
        }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tokens_fragment_menu, menu);
    }

    @Override
    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tokens, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.active:
                        mPresenter.setFiltering(TokensFilterType.ACTIVE_TOKENS);
                        break;
                    case R.id.completed:
                        mPresenter.setFiltering(TokensFilterType.COMPLETED_TOKENS);
                        break;
                    default:
                        mPresenter.setFiltering(TokensFilterType.ALL_TOKENS);
                        break;
                }
                mPresenter.loadTokens(false);
                return true;
            }
        });

        popup.show();
    }


    @Override
    public void setLoadingIndicator(final boolean active) {

        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        // Make sure setRefreshing() is called after the layout is done with everything else.
        srl.post(new Runnable() {
            @Override
            public void run() {
                srl.setRefreshing(active);
            }
        });
    }

    @Override
    public void showTokens(List<Token> Tokens) {
        mTokensAdapter.replaceData(Tokens);

        mTokensView.setVisibility(View.VISIBLE);
        mNoTokensView.setVisibility(View.GONE);
    }

    @Override
    public void showNoActiveTokens() {
        showNoTokensViews(
                getResources().getString(R.string.no_tokens_active),
                R.drawable.ic_check_circle_24dp,
                false
        );
    }

    @Override
    public void showNoTokens() {
        showNoTokensViews(
                getResources().getString(R.string.no_tokens_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        );
    }

    @Override
    public void showNoCompletedTokens() {
        showNoTokensViews(
                getResources().getString(R.string.no_tokens_completed),
                R.drawable.ic_verified_user_24dp,
                false
        );
    }

    private void showNoTokensViews(String mainText, int iconRes, boolean showAddView) {
        mTokensView.setVisibility(View.GONE);
        mNoTokensView.setVisibility(View.VISIBLE);

        mNoTokenMainView.setText(mainText);
        mNoTokenIcon.setImageDrawable(getResources().getDrawable(iconRes));
    }

    @Override
    public void showActiveFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_active));
    }

    @Override
    public void showCompletedFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_completed));
    }

    @Override
    public void showAllFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_all));
    }

    @Override
    public void showLoadingTokensError() {
        showMessage(getString(R.string.loading_tokens_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    /**
     * Listener for clicks on Tokens in the ListView.
     */
    TokenItemListener mItemListener = new TokenItemListener() {
        @Override
        public void handleTokenStatus(Token activatedToken) {
            if (getActivity() != null) {
                if (isResumed()) {
                    //Play sound and vibrate
                    SoundUtil.playSound(getActivity());
                } else {
                    Bundle b = new Bundle();
                    b.putString(ApplicationConstants.TOKEN_ID_KEY, activatedToken.getuId());
                    NotificationUtil.sendNotification(getActivity(), "Update for Token number: " + activatedToken.getTokenNumber(),
                            "Your turn has arrived. Kindly proceed to the counter number: " + activatedToken.getCounter(), b);
                }
            }
        }
    };

    public interface TokenItemListener {
        void handleTokenStatus(Token activatedToken);
    }
}
