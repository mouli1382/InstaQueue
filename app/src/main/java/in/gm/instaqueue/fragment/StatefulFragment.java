package in.gm.instaqueue.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.gm.instaqueue.prefs.SharedPrefs;

/**
 * Headless fragment which maintains the db/ preference instances shared across the activities and fragments.
 */
public class StatefulFragment extends Fragment {
    private static final String TAG = "StatefulFragment";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabaseReference;
    private SharedPrefs mSharedPrefs;

    public StatefulFragment() {
        // Required empty public constructor
    }

    public static StatefulFragment createInstance() {
        StatefulFragment fragment = new StatefulFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mSharedPrefs = SharedPrefs.getInstance(getActivity());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                //ToDo remove these.
                if (user != null) {
                    Log.i(TAG, "onAuthStateChanged:signed_in:");
                } else {
                    Log.i(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


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

    public SharedPrefs getSharedPrefs() {
        return mSharedPrefs;
    }

    public String getUserPhoneNumber() {
        if (mSharedPrefs != null) {
            return mSharedPrefs.getSting(SharedPrefs.PHONE_NUMBER_KEY);
        } else {
            return "";
        }
    }
}
