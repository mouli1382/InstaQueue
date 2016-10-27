package in.gm.instaqueue.fragment;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import in.gm.instaqueue.R;
import in.gm.instaqueue.adapter.TokenRecyclerViewHolder;
import in.gm.instaqueue.firebase.FirebaseManager;
import in.gm.instaqueue.model.Token;

public class LandingFragment extends BaseFragment {

    public static final String LANDING_FRAGMENT_TAG = "landing_fragment_tag";
    private RecyclerView mTokenRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;

    private FirebaseRecyclerAdapter<Token, TokenRecyclerViewHolder>
            mFirebaseAdapter;

    public static LandingFragment createInstance() {
        LandingFragment fragment = new LandingFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_landing, container, false);

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) root.findViewById(R.id.progressBar);
        mTokenRecyclerView = (RecyclerView) root.findViewById(R.id.tokenRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mLinearLayoutManager.setStackFromEnd(true);
        mTokenRecyclerView.setLayoutManager(mLinearLayoutManager);

//        mTokenRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
//                getApplicationContext()
//        ));

        FirebaseUser firebaseUser = getCurrentUser();
        if (firebaseUser == null) {
            getActivity().finish();
            return null;
        }

        String phoneNumber = getUserPhoneNumber();

        // New child entries
        DatabaseReference databaseReference = mFirebaseManager.getDatabaseReference();
        Query query = databaseReference
                .child(FirebaseManager.TOKENS_CHILD)
                .orderByChild("phoneNumber")
                .equalTo(phoneNumber);
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Token,
                TokenRecyclerViewHolder>(
                Token.class,
                R.layout.item_token,
                TokenRecyclerViewHolder.class,
                query) {

            @Override
            protected void populateViewHolder(TokenRecyclerViewHolder viewHolder, Token token, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.tokenTextView.setText(token.getTokenNumber() + "");
                viewHolder.tokenStoreNameView.setText(token.getStoreId());
//                if (token.getEmail() == null) {
//                    viewHolder.messengerImageView
//                            .setImageDrawable(ContextCompat
//                                    .getDrawable(OnBoardingActivity.this,
//                                            R.drawable.ic_account_circle_black_36dp));
//                } else {
//                    Glide.with(OnBoardingActivity.this)
//                            .load(token.getEmail())
//                            .into(viewHolder.messengerImageView);
//                }

                if (token.needsBuzz()) {
                    playSound();
                }
            }
        };

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }
        });

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int tokenCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (tokenCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mTokenRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mTokenRecyclerView.setLayoutManager(mLinearLayoutManager);
        mTokenRecyclerView.setAdapter(mFirebaseAdapter);

        return root;
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
