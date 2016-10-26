package in.gm.instaqueue.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseManager {

    public static final String TOKENS_CHILD = "tokens";
    public static final String TOKENS_HISTORY_CHILD = "token-history";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseReference;

    public FirebaseManager() {
        // Required empty public constructor
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        mAuth = FirebaseAuth.getInstance();
//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//                //ToDo remove these.
//                if (user != null) {
//                    Log.i(TAG, "onAuthStateChanged:signed_in:");
//                } else {
//                    Log.i(TAG, "onAuthStateChanged:signed_out");
//                }
//            }
//        };
//        mAuth.addAuthStateListener(mAuthListener);
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        if (mAuthListener != null) {
//            mAuth.removeAuthStateListener(mAuthListener);
//        }
//    }


    public FirebaseAuth getAuthInstance() {
        return mAuth;
    }

    public FirebaseUser getCurrentUser() {
        if (mAuth != null) {
            return mAuth.getCurrentUser();
        } else {
            return null;
        }
    }

    public DatabaseReference getDatabaseReference() {
        return mDatabaseReference;
    }

}

