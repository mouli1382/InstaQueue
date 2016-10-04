package in.gm.instaqueue.activity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Token, TokenRecyclerViewHolder>
            mFirebaseAdapter;

    private static long tokenNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing);

        // Initialize ProgressBar and RecyclerView.
        mMainView = findViewById(R.id.mainView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTokenRecyclerView = (RecyclerView) findViewById(R.id.tokenRecyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);
        mTokenRecyclerView.setLayoutManager(mLinearLayoutManager);

        // New child entries
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
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
//                if (token.getPhotoUrl() == null) {
//                    viewHolder.messengerImageView
//                            .setImageDrawable(ContextCompat
//                                    .getDrawable(MainActivity.this,
//                                            R.drawable.ic_account_circle_black_36dp));
//                } else {
//                    Glide.with(MainActivity.this)
//                            .load(token.getPhotoUrl())
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
                CharSequence phone = mPhoneNumberEditText.getText();
                if (Patterns.PHONE.matcher(phone).matches()) {
                    Token token = new Token(1234 + "",
                            phone.toString(),
                            ++tokenNumber,
                            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "");
                    mFirebaseDatabaseReference.child(FirebaseManager.TOKENS_CHILD)
                            .push().setValue(token);
                    mPhoneNumberEditText.setText("");
                } else {
                    showMessage(mMainView, R.string.invalid_phone_number);
                }
            }
        });

    }
}
