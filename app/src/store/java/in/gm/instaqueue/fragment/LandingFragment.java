package in.gm.instaqueue.fragment;


public class LandingFragment extends BaseFragment {

//    public static final String LANDING_FRAGMENT_TAG = "landing_fragment_tag";
//
//    private Button mGenerateButton;
//    private RecyclerView mTokenRecyclerView;
//    private LinearLayoutManager mLinearLayoutManager;
//    private ProgressBar mProgressBar;
//    private EditText mPhoneNumberEditText;
//    private View mMainView;
//
//    private DatabaseReference mFirebaseDatabaseReference;
//    private FirebaseRecyclerAdapter<Token, TokenRecyclerViewHolder>
//            mFirebaseAdapter;
//
//    private FirebaseUser mFirebaseUser;
//    private ProgressDialog mProgressDialog;
//
//    public static LandingFragment createInstance() {
//        LandingFragment fragment = new LandingFragment();
//        return fragment;
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View root = inflater.inflate(R.layout.fragment_landing, container, false);
//
//        mFirebaseUser = getCurrentUser();
//        if (mFirebaseUser == null) {
//            getActivity().finish();
//            return null;
//        }
//
//        // Initialize ProgressBar and RecyclerView.
//        mMainView = root.findViewById(R.id.mainView);
//        mProgressBar = (ProgressBar) root.findViewById(R.id.progressBar);
//        mTokenRecyclerView = (RecyclerView) root.findViewById(R.id.tokenRecyclerView);
//        mLinearLayoutManager = new LinearLayoutManager(getActivity());
//        mLinearLayoutManager.setStackFromEnd(true);
//        mTokenRecyclerView.setLayoutManager(mLinearLayoutManager);
//
//        initSwipe();
//
//        mProgressDialog = new ProgressDialog(getActivity());
//        mProgressDialog.setMessage("Fetching token....");
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//
//        // New child entries
//        mFirebaseDatabaseReference = getDatabaseReference();
//
//        Query query = mFirebaseDatabaseReference
//                .child(FirebaseManager.TOKENS_CHILD)
//                .orderByChild("storeId")
//                .equalTo(mFirebaseUser.getUid());
//        mFirebaseAdapter = new FirebaseRecyclerAdapter<Token,
//                TokenRecyclerViewHolder>(
//                Token.class,
//                R.layout.item_token,
//                TokenRecyclerViewHolder.class,
//                query) {
//
//            @Override
//            protected void populateViewHolder(TokenRecyclerViewHolder viewHolder, final Token token, int position) {
//                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//                viewHolder.tokenTextView.setText(token.getTokenNumber() + "");
////                if (token.getEmail() == null) {
////                    viewHolder.messengerImageView
////                            .setImageDrawable(ContextCompat
////                                    .getDrawable(OnBoardingActivity.this,
////                                            R.drawable.ic_account_circle_black_36dp));
////                } else {
////                    Glide.with(OnBoardingActivity.this)
////                            .load(token.getEmail())
////                            .into(viewHolder.messengerImageView);
////                }
//
//
//                final DatabaseReference tokenRef = getRef(position);
//                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        tokenRef.child("status").setValue(Token.Status.READY.ordinal());
//                        tokenRef.child("buzzCount").setValue(token.getBuzzCount() + 1);
//                    }
//                });
//            }
//        };
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//            }
//        });
//
//        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
//                int lastVisiblePosition =
//                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
//                // If the recycler view is initially being loaded or the
//                // user is at the bottom of the list, scroll to the bottom
//                // of the list to show the newly added message.
//                if (lastVisiblePosition == -1 ||
//                        (positionStart >= (friendlyMessageCount - 1) &&
//                                lastVisiblePosition == (positionStart - 1))) {
//                    mTokenRecyclerView.scrollToPosition(positionStart);
//                }
//            }
//        });
//
//        mTokenRecyclerView.setLayoutManager(mLinearLayoutManager);
//        mTokenRecyclerView.setAdapter(mFirebaseAdapter);
//
//        mPhoneNumberEditText = (EditText) root.findViewById(R.id.phoneEditText);
//        mPhoneNumberEditText.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.toString().trim().length() > 0) {
//                    mGenerateButton.setEnabled(true);
//                } else {
//                    mGenerateButton.setEnabled(false);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//            }
//        });
//
//        mGenerateButton = (Button) root.findViewById(R.id.generateButton);
//        mGenerateButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                ConnectivityManager cm =
//                        (ConnectivityManager) _application.getSystemService(Context.CONNECTIVITY_SERVICE);
//
//                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//                boolean isConnected = activeNetwork != null &&
//                        activeNetwork.isConnectedOrConnecting();
//
//
//                if (!isConnected) {
//                    showMessage(mMainView, R.string.no_connectivity);
//                    return;
//                }
//                CharSequence phone = mPhoneNumberEditText.getText();
//
//                if (!Patterns.PHONE.matcher(phone).matches()) {
//                    showMessage(mMainView, R.string.invalid_phone_number);
//                    return;
//                }
//
//
//                mProgressDialog.setIndeterminate(true);
//                mProgressDialog.show();
//                String phoneNum = mSharedPrefs.getSting(PHONE_NUMBER_KEY);
//                if (phoneNum.isEmpty()) {
//                    showMessage(mMainView, "Please authenticate again");
//                    return;
//                }
//                DatabaseReference tokenCounterRef = mFirebaseDatabaseReference
//                        .child("store")
//                        .child(getCurrentUser().getUid())
//                        .child("tokenCounter");
//                tokenCounterRef.runTransaction(new Transaction.Handler() {
//                    @Override
//                    public Transaction.Result doTransaction(MutableData mutableData) {
//                        Long currentValue = mutableData.getValue(Long.class);
//                        if (currentValue == null) {
//                            mutableData.setValue(1);
//                        } else {
//                            mutableData.setValue(currentValue + 1);
//                        }
//
//                        return Transaction.success(mutableData);
//                    }
//
//                    @Override
//                    public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
//                        System.out.println("Transaction completed");
//                        if (databaseError == null) {
//                            if (committed) {
//                                mProgressDialog.hide();
//                                showMessage(mMainView, R.string.token_incremented);
//                                //ToDo Remove the country code hardcoding later.
//                                Long currentToken = (Long) dataSnapshot.getValue();
//                                {
//                                    Token token = new Token(mFirebaseUser.getUid(),
//                                            "+91" + mPhoneNumberEditText.getText(),
//                                            currentToken,
//                                            TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "");
//                                    mFirebaseDatabaseReference.child(FirebaseManager.TOKENS_CHILD)
//                                            .push().setValue(token);
//                                    mPhoneNumberEditText.setText("");
//                                }
//                            } else {
//                                showMessage(mMainView, R.string.token_could_not_be_incremented);
//                                mProgressDialog.hide();
//                            }
//                        } else {
//                            mProgressDialog.hide();
//                            showMessage(mMainView, R.string.no_connectivity);
//                        }
//
//                    }
//                });
//
//
//            }
//        });
//
//        return root;
//    }
//
//    private Paint p = new Paint();
//
//    private void initSwipe() {
//        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//
//            @Override
//            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//                return false;
//            }
//
//            @Override
//            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
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
//                        .child(FirebaseManager.TOKENS_HISTORY_CHILD)
//                        .push()
//                        .setValue(token);
//                tokenRef.removeValue();
//                mFirebaseAdapter.notifyItemRemoved(position);
//            }
//
//            @Override
//            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
//
//                Bitmap icon;
//                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
//
//                    View itemView = viewHolder.itemView;
//                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
//                    float width = height / 3;
//
//                    if (dX > 0) {
//                        p.setColor(Color.parseColor("#388E3C"));
//                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
//                        c.drawRect(background, p);
//                        icon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_compass);
//                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
//                        c.drawBitmap(icon, null, icon_dest, p);
//                    } else {
//                        p.setColor(Color.parseColor("#D32F2F"));
//                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
//                        c.drawRect(background, p);
//                        icon = BitmapFactory.decodeResource(getResources(), android.R.drawable.ic_menu_close_clear_cancel);
//                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
//                        c.drawBitmap(icon, null, icon_dest, p);
//                    }
//                }
//                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
//            }
//        };
//        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
//        itemTouchHelper.attachToRecyclerView(mTokenRecyclerView);
//    }
//    private void removeView(){
//        if(view.getParent()!=null) {
//            ((ViewGroup) view.getParent()).removeView(view);
//        }
//    }

}