package in.mobifirst.tagtree.addedittoken;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.fragment.BaseFragment;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.util.NetworkConnectionUtils;
import in.mobifirst.tagtree.view.ProgressDialogFragment;


public class AddEditTokenFragment extends BaseFragment implements AddEditTokenContract.View {

    private AddEditTokenContract.Presenter mPresenter;

    @Inject
    IQSharedPreferences iqSharedPreferences;

    @Inject
    protected NetworkConnectionUtils mNetworkConnectionUtils;

    @Inject
    protected ProgressDialogFragment mProgressDialogFragment;

    private TextInputLayout mPhoneNumberInputLayout;
    private TextInputEditText mPhoneNumberEditText;
    private Spinner mCounterSpinner;
    private int mNumberOfCounters;

    public static AddEditTokenFragment newInstance() {
        return new AddEditTokenFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((IQStoreApplication) getActivity().getApplicationContext()).getApplicationComponent()
                .inject(this);
    }

    private BroadcastReceiver mNetworkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra(TTLocalBroadcastManager.NETWORK_STATUS_KEY, false);

            if (!isConnected && getView() != null) {
                updateProgress(false);
                showNetworkError(getView());
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!mNetworkConnectionUtils.isConnected()) {
            showNetworkError(getView());
        }
        TTLocalBroadcastManager.registerReceiver(getActivity(), mNetworkBroadcastReceiver, TTLocalBroadcastManager.NETWORK_INTENT_ACTION);
//        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        TTLocalBroadcastManager.unRegisterReceiver(getActivity(), mNetworkBroadcastReceiver);
//        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(@NonNull AddEditTokenContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNetworkConnectionUtils.isConnected()) {
                    if (validateInput()) {
                        //ToDo hardcoding to IND country code as of now.
                        mPresenter.addNewToken("+91" + mPhoneNumberEditText.getText().toString(),
                                mNumberOfCounters > 1 ? (mCounterSpinner.getSelectedItemPosition() + 1) : 1);
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_addtoken, container, false);
        mPhoneNumberEditText = (TextInputEditText) root.findViewById(R.id.add_phone_number);
        mPhoneNumberInputLayout = (TextInputLayout) root.findViewById(R.id.phoneNumberInputLayout);

        mPhoneNumberEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    CharSequence phone = mPhoneNumberEditText.getText();
                    if (TextUtils.isEmpty(phone)) {
                        mPhoneNumberInputLayout.setError(getString(R.string.invalid_phone_number));
                    } else if (!Patterns.PHONE.matcher(phone).matches()) {
                        mPhoneNumberInputLayout.setError(getString(R.string.invalid_phone_number));
                    } else {
                        mPhoneNumberInputLayout.setError("");
                    }
                }
                return true;
            }
        });

        mNumberOfCounters = iqSharedPreferences.getInt(ApplicationConstants.NUMBER_OF_COUNTERS_KEY);

        mCounterSpinner = (Spinner) root.findViewById(R.id.counter_spinner);
        if (mNumberOfCounters > 1) {
            // Create an ArrayAdapter using the string array and a default spinner layout
            String[] items = new String[mNumberOfCounters];
            for (int i = 0; i < mNumberOfCounters; i++) {
                items[i] = "Counter-" + (i + 1);
            }
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            mCounterSpinner.setAdapter(adapter);
            mCounterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String counter = adapter.getItem(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            mCounterSpinner.setVisibility(View.GONE);
        }

        setRetainInstance(true);
        return root;
    }

    private boolean validateInput() {
        CharSequence phone = mPhoneNumberEditText.getText();
        if (TextUtils.isEmpty(phone) || phone.toString().length() != 10) {
            mPhoneNumberInputLayout.setError(getString(R.string.invalid_phone_number));
            return false;
        } else {
            mPhoneNumberInputLayout.setError("");
        }

        return true;
    }

    @Override
    public void showEmptyTokenError() {
        Snackbar.make(getView(), getString(R.string.empty_token_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTokensList(String lastCreated) {
        //todo: Find a better way to avoid crash
        if (getActivity() == null)
            return;
        getActivity().setResult(Activity.RESULT_OK, new Intent().putExtra(ApplicationConstants.LAST_CREATED_TOKEN, lastCreated));
        getActivity().finish();
    }

    @Override
    public void updateProgress(boolean show) {
        if (show) {
            mProgressDialogFragment.show(getActivity().getSupportFragmentManager(), "tokenCreation");
        } else {
            mProgressDialogFragment.dismissAllowingStateLoss();
        }
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
