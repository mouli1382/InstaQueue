package in.gm.instaqueue.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

import in.gm.instaqueue.R;
import in.gm.instaqueue.adapter.TokenRecyclerViewHolder;
import in.gm.instaqueue.firebase.FirebaseManager;
import in.gm.instaqueue.model.Token;

public class LandingActivity extends BaseActivity {

    private Button mGenerateButton;
    private RecyclerView mTokenRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private ProgressBar mProgressBar;
    private EditText mPhoneNumberEditText;
    private View mMainView;
    private long mLastToken;

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Token, TokenRecyclerViewHolder>
            mFirebaseAdapter;

    private FirebaseUser mFirebaseUser;
    private ProgressDialog mProgressDialog;

    public static void start(Context caller) {
        Intent intent = new Intent(caller, LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser == null) {
            finish();
            return;
        }

        // Initialize ProgressBar and RecyclerView.
        mMainView = findViewById(R.id.mainView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTokenRecyclerView = (RecyclerView) findViewById(R.id.tokenRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mTokenRecyclerView.setLayoutManager(mLinearLayoutManager);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Fetching token....");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        //This code is for query: for future use serves as a sample
        /*Query qu = mFirebaseDatabaseReference.child(FirebaseManager.TOKENS_CHILD).orderByChild("tokenNumber").limitToLast(1);

        qu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists())
                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Token,
                TokenRecyclerViewHolder>(
                Token.class,
                R.layout.item_token,
                TokenRecyclerViewHolder.class,
                mFirebaseDatabaseReference.child(FirebaseManager.TOKENS_CHILD)) {

            @Override
            protected void populateViewHolder(TokenRecyclerViewHolder viewHolder, Token token, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.tokenTextView.setText(token.getTokenNumber() + "");
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

        mPhoneNumberEditText = (EditText) findViewById(R.id.phoneEditText);
        mPhoneNumberEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mGenerateButton.setEnabled(true);
                } else {
                    mGenerateButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mGenerateButton = (Button) findViewById(R.id.generateButton);
        mGenerateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager cm =
                        (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();


                if (!isConnected) {
                    showMessage(mMainView, R.string.no_connectivity);
                    return;
                }
                CharSequence phone = mPhoneNumberEditText.getText();

                if (!Patterns.PHONE.matcher(phone).matches()) {
                    showMessage(mMainView, R.string.invalid_phone_number);
                    return;
                }


                mProgressDialog.setIndeterminate(true);
                mProgressDialog.show();
                DatabaseReference tokenCounterRef = mFirebaseDatabaseReference.child("tokenCounter");
                tokenCounterRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        Long currentValue = mutableData.getValue(Long.class);
                        if (currentValue == null) {
                            mutableData.setValue(1);
                        } else {
                            mutableData.setValue(currentValue + 1);
                        }

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                        System.out.println("Transaction completed");
                        if (databaseError == null) {
                            if (committed) {
                                mProgressDialog.hide();
                                showMessage(mMainView, R.string.token_incremented);
                                //ToDo Remove the country code hardcoding later.
                                Long currentToken = (Long) dataSnapshot.getValue();
                                {
                                    Token token = new Token(mFirebaseUser.getUid(),
                                            "+91" + mPhoneNumberEditText.getText(),
                                            currentToken,
                                            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "");
                                    mFirebaseDatabaseReference.child(FirebaseManager.TOKENS_CHILD)
                                            .push().setValue(token);
                                    mPhoneNumberEditText.setText("");
                                }
                            } else {
                                showMessage(mMainView, R.string.token_could_not_be_incremented);
                                mProgressDialog.hide();
                            }
                        } else {
                            mProgressDialog.hide();
                            showMessage(mMainView, R.string.no_connectivity);
                        }

                    }
                });


            }
        });

    }
}
