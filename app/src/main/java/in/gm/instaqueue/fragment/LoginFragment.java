package in.gm.instaqueue.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.digits.sdk.android.DigitsAuthButton;
import com.google.android.gms.common.SignInButton;

import in.gm.instaqueue.R;
import in.gm.instaqueue.activity.DigitsSignInActivity;
import in.gm.instaqueue.activity.GoogleSignInActivity;

public class LoginFragment extends BaseFragment {

    private static final String TAG = "LoginFragment";
    public static final String LOGIN_FRAGMENT_TAG = LoginFragment.class.getName();

    public LoginFragment() {
    }

    public static LoginFragment createInstance() {
        LoginFragment fragment = new LoginFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_login, container, false);

        SignInButton signInButton = (SignInButton) root.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadGoogleSignInFragment();
            }
        });

        Button digitsAuthButton = (Button) root.findViewById(R.id.auth_button);
        digitsAuthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDigitsSignInFragment();
            }
        });
        return root;
    }

    private void loadGoogleSignInFragment() {
        getActivity().finish();
        GoogleSignInActivity.start(getActivity());
    }

    private void loadDigitsSignInFragment() {
        getActivity().finish();
        DigitsSignInActivity.start(getActivity());
    }
}
