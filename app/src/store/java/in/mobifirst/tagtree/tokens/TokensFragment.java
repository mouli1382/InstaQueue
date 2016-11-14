package in.mobifirst.tagtree.tokens;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
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
import java.util.Collection;
import java.util.List;
import java.util.Map;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.addedittoken.AddEditTokenActivity;
import in.mobifirst.tagtree.model.Token;

public class TokensFragment extends Fragment implements TokensContract.View {

    private TokensContract.Presenter mPresenter;

    //private SnapAdapter mSnapAdapter;
    private TokensAdapter mSnapAdapter;

    private View mNoTokensView;

    private ImageView mNoTokenIcon;

    private TextView mNoTokenMainView;

    private TextView mNoTokenAddView;

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
       mSnapAdapter = new TokensAdapter(new ArrayList<Token>(0), mItemListener);
         //mSnapAdapter = new SnapAdapter();
    }


    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
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

        // Set up Tokens view
        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
//        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mSnapAdapter);

//        initSwipe(recyclerView);

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

        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);

        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewToken();
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
        swipeRefreshLayout.setScrollUpChild(recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadTokens(false);
            }
        });

        setHasOptionsMenu(true);

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
                mPresenter.loadTokens(true);
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
                mPresenter.loadTokens(false);
                return true;
            }
        });

        popup.show();
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
//        mSnapAdapter.replaceData(Tokens);

        mTokensView.setVisibility(View.VISIBLE);
        mNoTokensView.setVisibility(View.GONE);
    }

    @Override
    public void showTokens(Map<Integer, Collection<Token>> tokenMap) {
        //Todo: This is a temp change
        Integer key = 0;
        mSnapAdapter.replaceData((List)tokenMap.get(key));
        //Todo: temp change ends here
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

    @Override
    public void showNoCancelledTokens() {
        showNoTokensViews(
                getResources().getString(R.string.no_tokens_completed),
                R.drawable.ic_verified_user_24dp,
                false
        );
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_token_message));
    }

    private void showNoTokensViews(String mainText, int iconRes, boolean showAddView) {
        mTokensView.setVisibility(View.GONE);
        mNoTokensView.setVisibility(View.VISIBLE);

        mNoTokenMainView.setText(mainText);
        mNoTokenIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoTokenAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
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

    private Paint p = new Paint();

    private void initSwipe(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                int position = viewHolder.getAdapterPosition();
//                final DatabaseReference tokenRef = mFirebaseAdapter.getRef(position);
//                Token token = mFirebaseAdapter.getItem(position);
//                if (direction == ItemTouchHelper.LEFT) {
//                    //Mark it Cancelled
//                    token.setStatus(Token.Status.CANCELLED.ordinal());
//                    tokenRef.child("status").setValue(Token.Status.CANCELLED.ordinal());
//                } else {
//                    //Mark it Completed
//                    token.setStatus(Token.Status.COMPLETED.ordinal());
//                    tokenRef.child("status").setValue(Token.Status.COMPLETED.ordinal());
//                }
//
//                //Move it to token-history table.
//                mFirebaseDatabaseReference
//                        .child(FirebaseAuthenticationManager.TOKENS_HISTORY_CHILD)
//                        .push()
//                        .setValue(token);
//                tokenRef.removeValue();
//                mFirebaseAdapter.notifyItemRemoved(position);
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(Color.parseColor("#388E3C"));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_compass);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(Color.parseColor("#D32F2F"));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_close_clear_cancel);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

}
