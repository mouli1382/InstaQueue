package in.gm.instaqueue.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import in.gm.instaqueue.R;
import in.gm.instaqueue.adapter.TokenRecyclerViewHolder;
import in.gm.instaqueue.firebase.FirebaseManager;
import in.gm.instaqueue.model.Token;
import in.gm.instaqueue.prefs.SharedPrefs;

public class LandingActivity extends BaseActivity {

    private RecyclerView mTokenRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Token, TokenRecyclerViewHolder>
            mFirebaseAdapter;

    public static void start(Context caller) {
        Intent intent = new Intent(caller, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Initialize ProgressBar and RecyclerView.
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTokenRecyclerView = (RecyclerView) findViewById(R.id.tokenRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mTokenRecyclerView.setLayoutManager(mLinearLayoutManager);

//        mTokenRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
//                getApplicationContext()
//        ));

        FirebaseUser firebaseUser = getCurrentUser();
        if(firebaseUser == null) {
            finish();
            return;
        }

        String phoneNumber = getUserPhoneNumber();

        // New child entries
        mFirebaseDatabaseReference = getDatabaseReference();
        Query query = mFirebaseDatabaseReference
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
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mTokenRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mTokenRecyclerView.setLayoutManager(mLinearLayoutManager);
        mTokenRecyclerView.setAdapter(mFirebaseAdapter);
    }
}
