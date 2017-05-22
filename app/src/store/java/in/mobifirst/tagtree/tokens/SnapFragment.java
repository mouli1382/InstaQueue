package in.mobifirst.tagtree.tokens;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.addedittoken.AddEditTokenActivity;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.fragment.BaseFragment;
import in.mobifirst.tagtree.model.Service;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.util.NetworkConnectionUtils;

public class SnapFragment extends BaseFragment implements TokensContract.View {

    private static final String TAG = "SnapFragment";

    @Inject
    protected NetworkConnectionUtils mNetworkConnectionUtils;

    private TokensContract.Presenter mPresenter;

    private SnapAdapter mSnapAdapter;

    private View mNoTokensView;

    private ImageView mNoTokenIcon;

    private TextView mNoTokenMainView;

    private TextView mNoTokenAddView;

    private LinearLayout mTokensView;

    private TextView mFilteringLabelView;

    private ExpandableListView mExpandableListView;

    private boolean isOpenToday;

    public SnapFragment() {
        // Requires empty public constructor
    }

    public static SnapFragment newInstance() {
        return new SnapFragment();
    }

    private BroadcastReceiver mNetworkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra(TTLocalBroadcastManager.NETWORK_STATUS_KEY, false);

            if (!isConnected && getView() != null) {
                setLoadingIndicator(false);
                showNetworkError(getView());
            } else {
                if (getView() != null) {
                    startLoadingSlots(false);
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((IQStoreApplication) getActivity().getApplicationContext()).getApplicationComponent()
                .inject(this);
        mSnapAdapter = new SnapAdapter(getActivity());
    }

    @Override
    public void onResume() {
        super.onResume();
        //ToDo for now just check for the connectivity and show it in a snackbar.
        // Need to give user capability to refresh when SwipeToRefresh along with Rx and MVP is brought in.
        if (!mNetworkConnectionUtils.isConnected()) {
            setLoadingIndicator(false);
            showNetworkError(getView());
        } else {
            if (isOpenToday) {
                mPresenter.subscribe();
            } else {
                showNotWorkingToday();
            }
        }
        TTLocalBroadcastManager.registerReceiver(getActivity(), mNetworkBroadcastReceiver, TTLocalBroadcastManager.NETWORK_INTENT_ACTION);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
        TTLocalBroadcastManager.unRegisterReceiver(getActivity(), mNetworkBroadcastReceiver);
    }

    @Override
    public void setPresenter(@NonNull TokensContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode, data);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Spinner spinner = (Spinner) getActivity().findViewById(R.id.counter_spinner);
        spinner.setVisibility(View.GONE);

        Button dateButton = (Button) getActivity().findViewById(R.id.dateTextView);
        dateButton.setVisibility(View.VISIBLE);


        final Service service = getArguments().getParcelable(ApplicationConstants.SERVICE_UID);
        isOpenToday = getArguments().getBoolean(ApplicationConstants.IS_OPEN_TODAY);
        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

//        fab.setImageResource(R.drawable.ic_add);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //ToDo - TESTING CM
//                mPresenter.createAppointmentSlots(service.getId());
//            }
//        });
//        fab.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_issuetokens, container, false);

        mExpandableListView = (ExpandableListView) root.findViewById(R.id.expandableListView);
        mExpandableListView.setAdapter(mSnapAdapter);

        mFilteringLabelView = (TextView) root.findViewById(R.id.filteringLabel);
        mTokensView = (LinearLayout) root.findViewById(R.id.tokensLL);

        // Set up  no Tokens view
        mNoTokensView = root.findViewById(R.id.notokens);
        mNoTokenIcon = (ImageView) root.findViewById(R.id.notokensIcon);
        mNoTokenMainView = (TextView) root.findViewById(R.id.notokensMain);
        mNoTokenAddView = (TextView) root.findViewById(R.id.notokensAdd);
        mNoTokenAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddToken();
            }
        });

        // Set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(mExpandableListView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mNetworkConnectionUtils.isConnected()) {
                    startLoadingSlots(false);
                } else {
                    setLoadingIndicator(false);
                }
            }
        });

        return root;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                mPresenter.clearCompletedTokens();
                break;
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                startLoadingSlots(true);
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
                    case R.id.cancelled:
                        mPresenter.setFiltering(TokensFilterType.CANCELLED_TOKENS);
                        break;
                    default:
                        mPresenter.setFiltering(TokensFilterType.ALL_TOKENS);
                        break;
                }
                startLoadingSlots(false);
                return true;
            }
        });

        popup.show();
    }


    private void startLoadingSlots(boolean forceUpdate) {
        if (isOpenToday) {
            mPresenter.loadTokensMap(forceUpdate);
        } else {
            showNotWorkingToday();
            showMessage(getString(R.string.not_open_today));
        }
    }

    /**
     * Listener for clicks on Tokens in the ListView.
     */
    TokenItemListener mItemListener = new TokenItemListener() {
        @Override
        public void onTokenClick(Token clickedToken) {
            mPresenter.openTokenDetails(clickedToken);
        }

        @Override
        public void onCompleteTokenClick(Token completedToken) {
            mPresenter.completeToken(completedToken);
        }

        @Override
        public void onActivateTokenClick(Token activatedToken) {
            mPresenter.activateToken(activatedToken);
        }

        @Override
        public void onCancelTokenClick(Token cancelledToken) {
            mPresenter.cancelToken(cancelledToken);
        }
    };

    @Override
    public void setLoadingIndicator(final boolean active) {

        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        if (!mNetworkConnectionUtils.isConnected()) {
            // Make sure setRefreshing() is called after the layout is done with everything else.
            srl.post(new Runnable() {
                @Override
                public void run() {
                    srl.setRefreshing(false);
                }
            });
            return;
        }


        if (active) {
            if (!srl.isRefreshing()) {
                // Make sure setRefreshing() is called after the layout is done with everything else.
                srl.post(new Runnable() {
                    @Override
                    public void run() {
                        if (mNetworkConnectionUtils.isConnected()) {
                            srl.setRefreshing(true);
                        }
                    }
                });
            }
        } else {
            // Make sure setRefreshing() is called after the layout is done with everything else.
            srl.post(new Runnable() {
                @Override
                public void run() {
                    srl.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void showTokens(List<Token> Tokens) {
    }

    @Override
    public void showSnaps(List<Snap> snaps) {
        if (isActive()) {
            setLoadingIndicator(false);
            mSnapAdapter = new SnapAdapter(getActivity(), snaps);
            mExpandableListView.setAdapter(mSnapAdapter);
//            mSnapAdapter.replaceData(snaps);
            mTokensView.setVisibility(View.VISIBLE);
            mNoTokensView.setVisibility(View.GONE);
        }

//        //Send broadcast to TokenDisplayService here.
//        Intent intent = new Intent(TTLocalBroadcastManager.TOKEN_CHANGE_INTENT_ACTION);
//        intent.putParcelableArrayListExtra(TokenDisplayService.SNAP_LIST_INTENT_KEY,
//                (ArrayList<? extends Parcelable>) snaps);
//        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
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
                true
        );

//        //Send broadcast to TokenDisplayService here that there are no tokens.
//        Intent intent = new Intent(TTLocalBroadcastManager.TOKEN_CHANGE_INTENT_ACTION);
//        intent.putParcelableArrayListExtra(TokenDisplayService.SNAP_LIST_INTENT_KEY,
//                new ArrayList<Snap>());
//        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
    }

    @Override
    public void setServiceName(String serviceName) {
        mFilteringLabelView.setText(serviceName);
    }

    @Override
    public void showNoCompletedTokens() {
        showNoTokensViews(
                getResources().getString(R.string.no_tokens_completed),
                R.drawable.ic_verified_user_24dp,
                false
        );
    }

    @Override
    public void showNoCancelledTokens() {
        showNoTokensViews(
                getResources().getString(R.string.no_tokens_completed),
                R.drawable.ic_verified_user_24dp,
                false
        );
    }

    @Override
    public void showSuccessfullySavedMessage(String lastCreated) {
        Snackbar.make(getView(), Html.fromHtml(getString(R.string.successfully_saved_token_message) + " <b>" + lastCreated + "</b> created successfully!"), Snackbar.LENGTH_LONG).show();
    }

    private void showNoTokensViews(String mainText, int iconRes, boolean showAddView) {
        setLoadingIndicator(false);
        mTokensView.setVisibility(View.GONE);
        mNoTokensView.setVisibility(View.VISIBLE);

        mNoTokenMainView.setText(mainText);
        mNoTokenIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoTokenAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    private void showNotWorkingToday() {
        setLoadingIndicator(false);
        mTokensView.setVisibility(View.GONE);

        mNoTokensView.setVisibility(View.VISIBLE);
        mNoTokenMainView.setText(getString(R.string.not_open_today));

        mNoTokenIcon.setVisibility(View.GONE);
        mNoTokenAddView.setVisibility(View.GONE);
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
    public void showCancelledFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_completed));
    }

    @Override
    public void showAddToken() {
        Intent intent = new Intent(getContext(), AddEditTokenActivity.class);
        startActivityForResult(intent, AddEditTokenActivity.REQUEST_ADD_TOKEN);
    }

    @Override
    public void showTokenDetailsUi(String TokenId) {
        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
//        Intent intent = new Intent(getContext(), TokenDetailActivity.class);
//        intent.putExtra(TokenDetailActivity.EXTRA_Token_ID, TokenId);
//        startActivity(intent);
    }

    @Override
    public void showTokenMarkedComplete() {
        showMessage(getString(R.string.token_marked_complete));
    }

    @Override
    public void showTokenMarkedActive() {
        showMessage(getString(R.string.token_marked_active));
    }

    @Override
    public void showTokenMarkedCancel() {
        showMessage(getString(R.string.token_marked_cancel));
    }

    @Override
    public void showCompletedTokensCleared() {
        showMessage(getString(R.string.completed_tokens_cleared));
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


    public interface TokenItemListener {

        void onTokenClick(Token clickedToken);

        void onCompleteTokenClick(Token completedToken);

        void onActivateTokenClick(Token activatedToken);

        void onCancelTokenClick(Token activatedToken);
    }
}
