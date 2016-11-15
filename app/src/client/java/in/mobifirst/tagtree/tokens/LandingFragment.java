package in.mobifirst.tagtree.tokens;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.util.TimeUtils;

public class LandingFragment extends BaseFragment {

    @Inject
    protected FirebaseDatabaseManager mFirebaseDatabaseManager;

    private FirebaseRecyclerAdapter<Token, FirebaseViewHolder>
            mFirebaseAdapter;

    private View mNoTokensView;
    private ImageView mNoTokenIcon;
    private TextView mNoTokenMainView;
    private ProgressBar mProgressBar;

    public LandingFragment() {
        // Requires empty public constructor
    }

    public static LandingFragment newInstance() {
        return new LandingFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((IQClientApplication) getActivity().getApplicationContext()).getApplicationComponent()
                .inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_token, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.tokens_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        mProgressBar = (ProgressBar) root.findViewById(R.id.progressBar);

        // Set up  no Tokens view
        mNoTokensView = root.findViewById(R.id.notokens);
        mNoTokenIcon = (ImageView) root.findViewById(R.id.notokensIcon);
        mNoTokenMainView = (TextView) root.findViewById(R.id.notokensMain);

        setLoadingIndicator(true);

        Query query = mFirebaseDatabaseManager.getTokenRef();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Token,
                FirebaseViewHolder>(
                Token.class,
                R.layout.item_token,
                FirebaseViewHolder.class,
                query) {


            @Override
            protected void populateViewHolder(FirebaseViewHolder holder, final Token token, int position) {
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
            }
        };

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mFirebaseAdapter);

        mFirebaseAdapter
                .registerAdapterDataObserver(
                        new RecyclerView.AdapterDataObserver() {
                            @Override
                            public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
                                Token oldToken = mFirebaseAdapter.getItem(positionStart);
                                if (oldToken.needsBuzz()) {
                                    playSound();
                                }
                                super.onItemRangeChanged(positionStart, itemCount, payload);
                            }

                            @Override
                            public void onChanged() {
                                super.onChanged();
                                if(mFirebaseAdapter.getItemCount() == 0) {
                                    showNoTokens();
                                } else {
                                    showTokens();
                                }
                            }
                        }

                );

        query.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        setLoadingIndicator(false);
                        if(dataSnapshot == null) {
                            showNoTokens();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        setLoadingIndicator(false);
                        if(databaseError != null) {
                            showLoadingTokensError();
                        }
                    }
                }
        );

        return root;
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
        showMessage(getString(R.string.loading_tokens_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    public static class FirebaseViewHolder extends RecyclerView.ViewHolder {
        protected TextView mTokenNumber;
        protected TextView mStoreName;
        protected ImageView mImageView;
        protected TextView mDate;
        protected TextView mCounterNumber;
        protected TextView mTime;
        protected TextView mArea;

        public FirebaseViewHolder(View view) {
            super(view);
            mTokenNumber = (TextView) view.findViewById(R.id.tokenNumberText);
            mDate = (TextView) view.findViewById(R.id.tokenDate);
            mTime = (TextView) view.findViewById(R.id.tokenTime);
            mStoreName = (TextView) view.findViewById(R.id.tokenStoreName);
            mImageView = (ImageView) view.findViewById(R.id.storeImageView);
            mCounterNumber = (TextView) view.findViewById(R.id.counterNumber);
            mArea = (TextView) view.findViewById(R.id.areaName);
        }
    }

    private void playSound() {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), defaultSoundUri);
        ringtone.play();

        //Requires vibrate permission.
        //        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        //        vibrator.vibrate(200);
    }
}
