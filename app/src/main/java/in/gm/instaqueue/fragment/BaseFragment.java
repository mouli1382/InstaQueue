package in.gm.instaqueue.fragment;


import android.app.Application;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

import in.gm.instaqueue.app.IQApplication;
import in.gm.instaqueue.dagger.component.AppComponent;
import in.gm.instaqueue.firebase.FirebaseManager;
import in.gm.instaqueue.prefs.SharedPrefs;

public class BaseFragment extends Fragment {

    @Inject
    protected SharedPrefs mSharedPrefs;

    @Inject
    protected FirebaseManager mFirebaseManager;

    @Inject
    protected Application _application;

    public BaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getAppComponent().inject(this);
    }

    public AppComponent getAppComponent() {
        return ((IQApplication) getActivity().getApplicationContext()).getAppComponent();
    }
}
