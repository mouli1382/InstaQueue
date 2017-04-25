package in.mobifirst.tagtree.ftu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.fragment.BaseFragment;
import in.mobifirst.tagtree.model.Counter;
import in.mobifirst.tagtree.model.Store;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.tokens.TokensActivity;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.util.NetworkConnectionUtils;

import static android.app.Activity.RESULT_OK;


public class SettingsFragment extends BaseFragment implements SettingsContract.View {

    private static final int PICK_IMAGE_REQUEST = 9002;

    private SettingsContract.Presenter mPresenter;

    private String mProfilePicUri;

    private ImageView mStoreImageView;
    private ProgressBar mProgressBar;
    private Button mUploadButton;
    private FloatingActionButton fab;
    private TextInputEditText mStoreNameEditText;
    private TextInputEditText mStoreAreaEditText;
    private TextInputEditText mWebsiteEditText;
    private TextInputEditText mCountersEditText;
    private TextInputLayout mStoreNameTextInputLayout;
    private TextInputLayout mStoreAreaTextInputLayout;
    private TextInputLayout mStoreWebsiteTextInputLayout;
    private TextInputLayout mStoreCountersTextInputLayout;

    private Spinner mCounterSpinner;
    private int mNumberOfCounters = -1;
    private int mCounterPosition = -1;
    private View mCounterConfigLayout;
    private Button mCounterAddButton;
    private TextInputEditText mCounterNameEditText;
    private TextInputEditText mCounterCapacityEditText;
    private TextInputLayout mCounterNameTextInputLayout;
    private TextInputLayout mCounterCapacityTextInputLayout;
    private List<Counter> mCounterList;

    private byte[] bitmapData;

    @Inject
    IQSharedPreferences mIQSharedPreferences;

    @Inject
    protected FirebaseAuthenticationManager mFirebaseAuth;

    @Inject
    protected NetworkConnectionUtils mNetworkConnectionUtils;

    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    private BroadcastReceiver mNetworkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra(TTLocalBroadcastManager.NETWORK_STATUS_KEY, false);

            if (!isConnected && getView() != null) {
                showNetworkError(getView());
            } else {
                mPresenter.subscribe();
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
    }

    @Override
    public void onPause() {
        super.onPause();
        TTLocalBroadcastManager.unRegisterReceiver(getActivity(), mNetworkBroadcastReceiver);
    }

    @Override
    public void setPresenter(@NonNull SettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((IQStoreApplication) getActivity().getApplicationContext()).getApplicationComponent()
                .inject(this);

        mPresenter.subscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNetworkConnectionUtils.isConnected()) {
                    if (validateInput()) {
                        mIQSharedPreferences.putString(ApplicationConstants.WEBSITE_LOGO_URL_KEY, mProfilePicUri);
                        Store store = new Store(mStoreNameEditText.getText().toString(),
                                mStoreAreaEditText.getText().toString(),
                                mWebsiteEditText.getText() != null ? mWebsiteEditText.getText().toString() : "",
                                mProfilePicUri, Integer.parseInt(mCountersEditText.getText().toString()));
                        mPresenter.addStoreDetails(store);
                    }
                }
            }
        });

        //Get and load the gmail  profile pic
        mProfilePicUri = mIQSharedPreferences.getSting(ApplicationConstants.WEBSITE_LOGO_URL_KEY);
        loadLogo();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_onboarding, container, false);

        mStoreNameTextInputLayout = (TextInputLayout) root.findViewById(R.id.storeNameInputLayout);
        mStoreAreaTextInputLayout = (TextInputLayout) root.findViewById(R.id.storeAreaInputLayout);
        mStoreWebsiteTextInputLayout = (TextInputLayout) root.findViewById(R.id.storeWebsiteInputLayout);
        mStoreCountersTextInputLayout = (TextInputLayout) root.findViewById(R.id.storeCountersInputLayout);

        mStoreNameEditText = (TextInputEditText) root.findViewById(R.id.storeName);
        mStoreNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mStoreNameTextInputLayout.setError(getString(R.string.empty_store_name));
                    mStoreNameTextInputLayout.setErrorEnabled(true);
                } else {
                    mStoreNameTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mStoreAreaEditText = (TextInputEditText) root.findViewById(R.id.areaName);
        mStoreAreaEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mStoreAreaTextInputLayout.setError(getString(R.string.empty_store_area));
                    mStoreAreaTextInputLayout.setErrorEnabled(true);
                } else {
                    mStoreAreaTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mWebsiteEditText = (TextInputEditText) root.findViewById(R.id.website);
        mWebsiteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!TextUtils.isEmpty(charSequence) && !Patterns.WEB_URL.matcher(charSequence).matches()) {
                    mStoreWebsiteTextInputLayout.setError(getString(R.string.invalid_store_website));
                    mStoreWebsiteTextInputLayout.setErrorEnabled(true);
                } else {
                    mStoreWebsiteTextInputLayout.setErrorEnabled(false);
                    getLogoUriUsingClearbit();
                    loadLogo();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mCountersEditText = (TextInputEditText) root.findViewById(R.id.counters);
        mCountersEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mStoreCountersTextInputLayout.setError(getString(R.string.empty_store_counters));
                    mStoreCountersTextInputLayout.setErrorEnabled(true);
                } else {
                    int counterValue = Integer.parseInt(charSequence.toString());
                    if (counterValue > 0 && counterValue < 100) {
                        mStoreCountersTextInputLayout.setErrorEnabled(false);
                        mNumberOfCounters = counterValue;
                        loadSpinner();
                    } else {
                        mStoreCountersTextInputLayout.setError(getString(R.string.invalid_store_counters));
                        mStoreCountersTextInputLayout.setErrorEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mProgressBar = (ProgressBar) root.findViewById(R.id.logoProgress);
        mStoreImageView = (ImageView) root.findViewById(R.id.storeProfilePic);
        mUploadButton = (Button) root.findViewById(R.id.uploadButton);
        mUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressBar.setVisibility(View.VISIBLE);
                mPresenter.uploadFile(bitmapData);
            }
        });

        mStoreImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });


        mCounterConfigLayout = root.findViewById(R.id.counterConfigLayout);
        mCounterAddButton = (Button) root.findViewById(R.id.counterAddButton);
        mCounterAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCounterValues();
            }
        });

        mCounterSpinner = (Spinner) root.findViewById(R.id.counterSpinner);

        mCounterNameEditText = (TextInputEditText) root.findViewById(R.id.counterName);
        mCounterNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mCounterNameTextInputLayout.setError(getString(R.string.empty_store_name));
                    mCounterNameTextInputLayout.setErrorEnabled(true);
                } else {
                    mCounterNameTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mCounterCapacityEditText = (TextInputEditText) root.findViewById(R.id.counterMaxTokens);
        mCounterCapacityEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mCounterCapacityTextInputLayout.setError(getString(R.string.empty_store_name));
                    mCounterCapacityTextInputLayout.setErrorEnabled(true);
                } else {
                    mCounterCapacityTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        setRetainInstance(true);
        return root;
    }

    private void loadSpinner() {
        if (mNumberOfCounters > 1) {
            mCounterList = new ArrayList<>(mNumberOfCounters);
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
//                    String counter = adapter.getItem(i);
                    if (addCounterValues()) {
                        mCounterSpinner.setSelection(mCounterPosition);
                    } else {
                        mCounterPosition = i;
                        refreshCounterViews(i);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            mCounterSpinner.setVisibility(View.GONE);
//                    mCounterConfigLayout.setVisibility(View.VISIBLE);
        }
    }

    private void refreshCounterViews(int position) {
        if (position != -1 && mCounterList != null) {
            Counter counter = mCounterList.get(position);
            if (counter != null) {
                mCounterNameEditText.setText(counter.getCounterName());
                mCounterCapacityEditText.setText(counter.getCounterCapacity());
            } else {
                mCounterNameEditText.setText("");
                mCounterCapacityEditText.setText("");
            }
        }
    }

    private boolean addCounterValues() {
        if (mCounterPosition != -1) {
            if (validateCounterValues()) {
                mCounterList.add(mCounterPosition,
                        new Counter(mCounterPosition,
                                mCounterNameEditText.getText().toString(),
                                Integer.parseInt(mCounterCapacityEditText.getText().toString())));
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private void getLogoUriUsingClearbit() {
        mProfilePicUri = mIQSharedPreferences.getSting(ApplicationConstants.WEBSITE_LOGO_URL_KEY);

        CharSequence website = mWebsiteEditText.getText();
        if (TextUtils.isEmpty(website))
            return;

        String url = website.toString();
        if (url != null && !url.startsWith("http") && !url.startsWith("https")) {
            url = "http://" + url;
        }
        String domain = Uri.parse(url).getHost();
        if (!TextUtils.isEmpty(domain)) {
            mProfilePicUri = String.format("https://logo.clearbit.com/%1$s", domain.startsWith("www.") ? domain.substring(4) : domain);
        }
    }

    private void loadLogo() {
        if (!TextUtils.isEmpty(mProfilePicUri)) {
            mStoreImageView.setEnabled(false);
            fab.setEnabled(false);
            Glide.with(getActivity())
                    .load(mProfilePicUri)
                    .asBitmap()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            mStoreImageView.setEnabled(true);
                            mProfilePicUri = null;
                            fab.setEnabled(true);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            mStoreImageView.setEnabled(true);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            bitmapData = baos.toByteArray();

                            mUploadButton.setEnabled(true);
                            fab.setEnabled(true);
                            return false;
                        }
                    })
                    .placeholder(R.mipmap.ic_launcher)
                    .into(mStoreImageView);
        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                mStoreImageView.setImageBitmap(bitmap);
                mStoreImageView.setDrawingCacheEnabled(true);
                mStoreImageView.buildDrawingCache();
                mStoreImageView.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                bitmapData = baos.toByteArray();

                mUploadButton.setEnabled(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void showUploadFailedError() {
        Snackbar.make(getView(), getString(R.string.upload_failed_error), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onFileUploadFinished(Uri uri) {
        mProgressBar.setVisibility(View.GONE);
        mProfilePicUri = uri.toString();
        mUploadButton.setEnabled(false);
    }

    @Override
    public void showEmptyStoreError() {
        Snackbar.make(getView(), getString(R.string.empty_store_details), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showAddStoreFailedError() {
        Snackbar.make(getView(), getString(R.string.add_store_failed), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTokensList(Store store) {
        //todo: Find a better way to avoid crash
        if (getActivity() == null)
            return;

        mIQSharedPreferences.putBoolean(ApplicationConstants.FTU_COMPLETED_KEY, true);
        mIQSharedPreferences.putString(ApplicationConstants.STORE_UID, mFirebaseAuth.getAuthInstance().getCurrentUser().getUid());
        store.persistStore(mIQSharedPreferences);

        TokensActivity.start(getActivity());
        getActivity().finish();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void populateStore(Store store) {
        if (isAdded() && store != null) {
            mStoreNameEditText.setText(store.getName());
            mStoreAreaEditText.setText(store.getArea());
            mWebsiteEditText.setText(store.getWebsite());
            mCountersEditText.setText(store.getNumberOfCounters() + "");
        }
    }

    private boolean validateCounterValues() {
        boolean result = false;
        CharSequence counterName = mCounterNameEditText.getText();
        if (TextUtils.isEmpty(counterName)) {
            mCounterNameTextInputLayout.setError(getString(R.string.empty_counter_name));
            mCounterNameTextInputLayout.setErrorEnabled(true);
            return result;
        }

        CharSequence counterCapacity = mCounterCapacityEditText.getText();
        if (TextUtils.isEmpty(counterCapacity)) {
            mCounterCapacityTextInputLayout.setError(getString(R.string.empty_counter_capacity));
            mCounterCapacityTextInputLayout.setErrorEnabled(true);
            return result;
        }


        mCounterNameTextInputLayout.setErrorEnabled(false);
        mCounterCapacityTextInputLayout.setErrorEnabled(false);

        return true;
    }


    private boolean validateInput() {
        boolean result = false;
        CharSequence storeName = mStoreNameEditText.getText();
        if (TextUtils.isEmpty(storeName)) {
            mStoreNameTextInputLayout.setError(getString(R.string.empty_store_name));
            mStoreNameTextInputLayout.setErrorEnabled(true);
            return result;
        }

        CharSequence storeArea = mStoreAreaEditText.getText();
        if (TextUtils.isEmpty(storeArea)) {
            mStoreAreaTextInputLayout.setError(getString(R.string.empty_store_area));
            mStoreAreaTextInputLayout.setErrorEnabled(true);
            return result;
        }

        CharSequence website = mWebsiteEditText.getText();
        if (!TextUtils.isEmpty(website) && !Patterns.WEB_URL.matcher(website).matches()) {
            mStoreWebsiteTextInputLayout.setError(getString(R.string.invalid_store_website));
            mStoreWebsiteTextInputLayout.setErrorEnabled(true);
            return result;
        }

        CharSequence counters = mCountersEditText.getText();
        if (TextUtils.isEmpty(counters)) {
            mStoreCountersTextInputLayout.setError(getString(R.string.empty_store_counters));
            mStoreCountersTextInputLayout.setErrorEnabled(true);
            return result;
        } else {
            int counterValue = Integer.parseInt(counters.toString());
            if (counterValue < 1 || counterValue > 100) {
                mStoreCountersTextInputLayout.setError(getString(R.string.invalid_store_counters));
                mStoreCountersTextInputLayout.setErrorEnabled(true);
                return result;
            }
        }

        if (!TextUtils.isEmpty(website) && TextUtils.isEmpty(mProfilePicUri)) {
            Snackbar.make(getView(), getString(R.string.website_not_exist), Snackbar.LENGTH_LONG).show();
            return result;
        }

        if (bitmapData == null || bitmapData.length == 0) {
            Snackbar.make(getView(), getString(R.string.upload_store_pic), Snackbar.LENGTH_LONG).show();
            return result;
        }

        if (TextUtils.isEmpty(mProfilePicUri)) {
            Snackbar.make(getView(), getString(R.string.upload_store_pic), Snackbar.LENGTH_LONG).show();
            return result;
        }

        CharSequence counterName = mCounterNameEditText.getText();
        if (TextUtils.isEmpty(counterName)) {
            mCounterNameTextInputLayout.setError(getString(R.string.empty_counter_name));
            mCounterNameTextInputLayout.setErrorEnabled(true);
            return result;
        }

        CharSequence counterCapacity = mCounterCapacityEditText.getText();
        if (TextUtils.isEmpty(counterCapacity)) {
            mCounterCapacityTextInputLayout.setError(getString(R.string.empty_counter_capacity));
            mCounterCapacityTextInputLayout.setErrorEnabled(true);
            return result;
        }

        mCounterNameTextInputLayout.setErrorEnabled(false);
        mCounterCapacityTextInputLayout.setErrorEnabled(false);

        mStoreNameTextInputLayout.setErrorEnabled(false);
        mStoreAreaTextInputLayout.setErrorEnabled(false);
        mStoreWebsiteTextInputLayout.setErrorEnabled(false);
        mStoreCountersTextInputLayout.setErrorEnabled(false);

        return true;
    }
}
