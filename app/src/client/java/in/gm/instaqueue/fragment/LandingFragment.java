package in.gm.instaqueue.fragment;

public class LandingFragment extends BaseFragment {

//    public static final String LANDING_FRAGMENT_TAG = "landing_fragment_tag";
//    private RecyclerView mTokenRecyclerView;
//    private LinearLayoutManager mLinearLayoutManager;
//    private ProgressBar mProgressBar;
//
//    private FirebaseRecyclerAdapter<Token, TokenRecyclerViewHolder>
//            mFirebaseAdapter;
//
//    public static LandingFragment createInstance() {
//        LandingFragment fragment = new LandingFragment();
//        return fragment;
//    }
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        View root = inflater.inflate(R.layout.fragment_landing, container, false);
//
//        // Initialize ProgressBar and RecyclerView.
//        mProgressBar = (ProgressBar) root.findViewById(R.id.progressBar);
//        mTokenRecyclerView = (RecyclerView) root.findViewById(R.id.tokenRecyclerView);
//        mLinearLayoutManager = new LinearLayoutManager(getActivity());
//        mLinearLayoutManager.setStackFromEnd(true);
//        mTokenRecyclerView.setLayoutManager(mLinearLayoutManager);
//
////        mTokenRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(
////                getApplicationContext()
////        ));
//
//        FirebaseUser firebaseUser = getCurrentUser();
//        if (firebaseUser == null) {
//            getActivity().finish();
//            return null;
//        }
//
//        String phoneNumber = getUserPhoneNumber();
//
//        // New child entries
//        DatabaseReference databaseReference = mFirebaseManager.getDatabaseReference();
//        Query query = databaseReference
//                .child(FirebaseManager.TOKENS_CHILD)
//                .orderByChild("phoneNumber")
//                .equalTo(phoneNumber);
//        mFirebaseAdapter = new FirebaseRecyclerAdapter<Token,
//                TokenRecyclerViewHolder>(
//                Token.class,
//                R.layout.item_token,
//                TokenRecyclerViewHolder.class,
//                query) {
//
//            @Override
//            protected void populateViewHolder(TokenRecyclerViewHolder viewHolder, Token token, int position) {
//                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
//                viewHolder.tokenTextView.setText(token.getTokenNumber() + "");
//                viewHolder.tokenStoreNameView.setText(token.getStoreId());
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
//                if (token.needsBuzz()) {
//                    playSound();
//                }
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
//                int tokenCount = mFirebaseAdapter.getItemCount();
//                int lastVisiblePosition =
//                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
//                // If the recycler view is initially being loaded or the
//                // user is at the bottom of the list, scroll to the bottom
//                // of the list to show the newly added message.
//                if (lastVisiblePosition == -1 ||
//                        (positionStart >= (tokenCount - 1) &&
//                                lastVisiblePosition == (positionStart - 1))) {
//                    mTokenRecyclerView.scrollToPosition(positionStart);
//                }
//            }
//        });
//
//        mTokenRecyclerView.setLayoutManager(mLinearLayoutManager);
//        mTokenRecyclerView.setAdapter(mFirebaseAdapter);
//
//        return root;
//    }
//
//    private void playSound() {
//        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        Ringtone ringtone = RingtoneManager.getRingtone(getActivity(), defaultSoundUri);
//        ringtone.play();
//
//        //Requires vibrate permission.
////        Vibrator vibrator = (Vibrator) getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
////        vibrator.vibrate(200);
//    }
}
