package in.mobifirst.tagtree.tokens;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.application.IQClientApplication;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.fragment.BaseFragment;
import in.mobifirst.tagtree.model.StoreCounter;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.tokens.viewholder.FirebaseViewHolder;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.util.NetworkConnectionUtils;
import in.mobifirst.tagtree.util.NotificationUtil;
import in.mobifirst.tagtree.util.SoundUtil;
import in.mobifirst.tagtree.util.TimeUtils;
import rx.Subscriber;

public class LandingFragment extends BaseFragment {
    private static final String TAG = "LandingFragment";

    @Inject
    protected FirebaseDatabaseManager mFirebaseDatabaseManager;

    @Inject
    protected NetworkConnectionUtils mNetworkConnectionUtils;

    private FirebaseRecyclerAdapter<Token, FirebaseViewHolder>
            mFirebaseAdapter;

    private View mNoTokensView;
    private ImageView mNoTokenIcon;
    private TextView mNoTokenMainView;
    private ProgressBar mProgressBar;
    private String mTokenId;

    public LandingFragment() {
        // Requires empty public constructor
    }

    public static LandingFragment newInstance(Bundle args) {
        LandingFragment landingFragment = new LandingFragment();
        landingFragment.setArguments(args);
        return landingFragment;
    }

    private BroadcastReceiver mNetworkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra(TTLocalBroadcastManager.NETWORK_STATUS_KEY, false);

            if (!isConnected && getView() != null) {
                setLoadingIndicator(false);
                showNetworkError(getView());
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((IQClientApplication) getActivity().getApplicationContext()).getApplicationComponent()
                .inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        //ToDo for now just check for the connectivity and show it in a snackbar.
        // Need to give user capability to refresh when SwipeToRefresh along with Rx and MVP is brought in.
        if (!mNetworkConnectionUtils.isConnected()) {
            showNetworkError(getView());
        }
        TTLocalBroadcastManager.registerReceiver(getActivity(), mNetworkBroadcastReceiver, TTLocalBroadcastManager.NETWORK_INTENT_ACTION);
    }

    @Override
    public void onPause() {
        super.onPause();
        TTLocalBroadcastManager.unRegisterReceiver(getActivity(), mNetworkBroadcastReceiver);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_token, container, false);

        final RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.tokens_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mProgressBar = (ProgressBar) root.findViewById(R.id.progressBar);

        // Set up  no Tokens view
        mNoTokensView = root.findViewById(R.id.notokens);
        mNoTokenIcon = (ImageView) root.findViewById(R.id.notokensIcon);
        mNoTokenMainView = (TextView) root.findViewById(R.id.notokensMain);

        setLoadingIndicator(true);

        final Bundle bundle = getArguments();

        Query query = mFirebaseDatabaseManager.getTokenRef();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Token,
                FirebaseViewHolder>(
                Token.class,
                R.layout.item_token,
                FirebaseViewHolder.class,
                query) {


            @Override
            protected void populateViewHolder(final FirebaseViewHolder holder, final Token token, int position) {
                showTokens();
                holder.mTokenNumber.setText(token.getTokenNumber() + "");
                holder.mStoreName.setText(token.getSenderName() + "");

                Glide.with(getActivity()).load(token.getSenderPic())
                        .centerCrop().placeholder(R.drawable.ic_account_circle_black_36dp).crossFade()
                        .into(holder.mImageView);

                holder.mDate.setText(TimeUtils.getDate(token.getTimestamp()));
                holder.mTime.setText(TimeUtils.getTime(token.getTimestamp()));
                holder.mCounterNumber.setText("" + token.getCounter());
                holder.mArea.setText(token.getAreaName());

                if (token.isActive()) {
                    holder.mTokenNumber.setTextColor(getResources().getColor(R.color.colorAccent));
                } else {
                    holder.mTokenNumber.setTextColor(getResources().getColor(R.color.common_google_signin_btn_text_dark_focused));
                }

                mFirebaseDatabaseManager.getCounterStatus(token, new Subscriber<StoreCounter>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {

                            }

                            @Override
                            public void onNext(StoreCounter storeCounter) {
                                Log.e(TAG, "passed token = " + token.getuId());
                                if (storeCounter != null) {
                                    holder.mCurrentActiveToken
                                            .setText(token.isActive() ? "It's your turn" : ("Currently running  " + storeCounter.getActivatedToken() + " ETA " + storeCounter.ETS(token.getTokenNumber())));
                                }
                            }
                        }

                );

                if (bundle != null)

                {
                    mTokenId = bundle.getString(ApplicationConstants.TOKEN_ID_KEY);
                }

                if (mTokenId != null && token.getuId().

                        equals(mTokenId)

                        )

                {
                    animateTokenNumber(holder.mTokenNumber);
                }
            }
        }

        ;

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mFirebaseAdapter);

        mFirebaseAdapter
                .registerAdapterDataObserver(
                        new RecyclerView.AdapterDataObserver()

                        {
                            @Override
                            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                                Token changedToken = mFirebaseAdapter.getItem(positionStart);
                                if (changedToken.needsBuzz()) {
                                    mTokenId = changedToken.getuId();
                                    handleTokenStatus(changedToken);
//                                    recyclerView.scrollToPosition(positionStart);
                                }
                                super.onItemRangeChanged(positionStart, itemCount, payload);
                            }

                            @Override
                            public void onItemRangeRemoved(int positionStart, int itemCount) {
                                if (mFirebaseAdapter.getItemCount() == 0) {
                                    showNoTokens();
                                }
                                super.onItemRangeRemoved(positionStart, itemCount);
                            }
                        }

                );

        query.addListenerForSingleValueEvent(
                new

                        ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                setLoadingIndicator(false);
                                if (dataSnapshot != null && dataSnapshot.getValue() == null) {
                                    showNoTokens();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                setLoadingIndicator(false);
                                if (databaseError != null) {
                                    showLoadingTokensError();
                                }
                            }
                        }

        );

        return root;
    }

    private void animateTokenNumber(View view) {
        //Animate the token number.
        AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(3);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        view.setAnimation(alphaAnimation);

        alphaAnimation.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFirebaseAdapter != null)
            mFirebaseAdapter.cleanup();
    }

    private void setLoadingIndicator(boolean active) {
        if (getView() == null) {
            return;
        }
        mProgressBar.setVisibility(active ? View.VISIBLE : View.GONE);
    }

    private void showTokens() {
        mProgressBar.setVisibility(View.GONE);
        mNoTokensView.setVisibility(View.INVISIBLE);
    }

    private void showNoTokens() {
        showNoTokensViews(
                getResources().getString(R.string.no_tokens_all),
                R.drawable.ic_assignment_turned_in_24dp
        );
    }

    private void showNoTokensViews(String mainText, int iconRes) {
        mProgressBar.setVisibility(View.GONE);
        mNoTokensView.setVisibility(View.VISIBLE);

        mNoTokenMainView.setText(mainText);
        mNoTokenIcon.setImageDrawable(getResources().getDrawable(iconRes));
    }

    private void showLoadingTokensError() {
        showMessage(getView(), getString(R.string.loading_tokens_error));
    }

    private void handleTokenStatus(Token token) {
        if (getActivity() != null) {
            if (isResumed()) {
                //Play sound and vibrate
                SoundUtil.playSound(getActivity());
            } else {
                Bundle b = new Bundle();
                b.putString(ApplicationConstants.TOKEN_ID_KEY, token.getuId());
                NotificationUtil.sendNotification(getActivity(), "Update for Token number: " + token.getTokenNumber(),
                        "Your turn has arrived. Kindly proceed to the counter number: " + token.getCounter(), b);
            }
        }
    }
}
