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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.fragment.BaseFragment;
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

    private byte[] bitmapData;

    @Inject
    IQSharedPreferences mIQSharedPreferences;

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
                        Store store = new Store(mStoreNameEditText.getText().toString(),
                                mStoreAreaEditText.getText().toString(),
                                mWebsiteEditText.getText().toString(),
                                mProfilePicUri, Integer.parseInt(mCountersEditText.getText().toString()));
                        mPresenter.addStoreDetails(store);
                    }
                }
            }
        });
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
//        mStoreNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
//                    CharSequence storeName = mStoreNameEditText.getText();
//                    if (TextUtils.isEmpty(storeName)) {
//                        mStoreNameTextInputLayout.setError(getString(R.string.empty_store_name));
//                    } else {
//                        mStoreNameTextInputLayout.setError("");
//                    }
//                    return true;
//                }
//                return false;
//            }
//        });
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
//        mStoreAreaEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
//                    CharSequence storeArea = mStoreAreaEditText.getText();
//                    if (TextUtils.isEmpty(storeArea)) {
//                        mStoreAreaTextInputLayout.setError(getString(R.string.empty_store_area));
//                    } else {
//                        mStoreAreaTextInputLayout.setError("");
//                    }
//                }
//                return true;
//            }
//        });
        mWebsiteEditText = (TextInputEditText) root.findViewById(R.id.website);
        mWebsiteEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mStoreWebsiteTextInputLayout.setError(getString(R.string.empty_store_website));
                    mStoreWebsiteTextInputLayout.setErrorEnabled(true);
                } else if (!Patterns.WEB_URL.matcher(charSequence).matches()) {
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
//        mWebsiteEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
//                    CharSequence website = mWebsiteEditText.getText();
//                    if (TextUtils.isEmpty(website)) {
//                        mStoreWebsiteTextInputLayout.setError(getString(R.string.empty_store_website));
//                    } else if (!Patterns.WEB_URL.matcher(website).matches()) {
//                        mStoreWebsiteTextInputLayout.setError(getString(R.string.invalid_store_website));
//                    } else {
//                        mStoreWebsiteTextInputLayout.setError("");
//                    }
//                }
//                return true;
//            }
//        });

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
//        mCountersEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
//                    CharSequence counters = mCountersEditText.getText();
//                    if (TextUtils.isEmpty(counters)) {
//                        mStoreCountersTextInputLayout.setError(getString(R.string.empty_store_counters));
//                    } else {
//                        int counterValue = Integer.parseInt(counters.toString());
//                        if (counterValue > 0 && counterValue < 100) {
//                            mStoreCountersTextInputLayout.setError("");
//                        } else {
//                            mStoreCountersTextInputLayout.setError(getString(R.string.invalid_store_counters));
//                        }
//                    }
//                }
//                return true;
//            }
//        });

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

        //Get and load the store profile pic
        mProfilePicUri = mIQSharedPreferences.getSting(ApplicationConstants.WEBSITE_LOGO_URL_KEY);
        loadLogo();

        mStoreImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        setRetainInstance(true);
        return root;
    }

    private void getLogoUriUsingClearbit() {
        mProfilePicUri = mIQSharedPreferences.getSting(ApplicationConstants.WEBSITE_LOGO_URL_KEY);
        if (!TextUtils.isEmpty(mProfilePicUri))
            return;

        String url = mWebsiteEditText.getText().toString().trim();
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
            Glide.with(getActivity())
                    .load(mProfilePicUri)
                    .asBitmap()
                    .listener(new RequestListener<String, Bitmap>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<Bitmap> target, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            mStoreImageView.setEnabled(true);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Bitmap resource, String model, Target<Bitmap> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            mStoreImageView.setEnabled(true);

                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            resource.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            bitmapData = baos.toByteArray();

                            mIQSharedPreferences.putString(ApplicationConstants.WEBSITE_LOGO_URL_KEY, mProfilePicUri);
                            mUploadButton.setEnabled(true);
                            return false;
                        }
                    })
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
        mIQSharedPreferences.putString(ApplicationConstants.WEBSITE_LOGO_URL_KEY, mProfilePicUri);
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
        if (TextUtils.isEmpty(website)) {
            mStoreWebsiteTextInputLayout.setError(getString(R.string.empty_store_website));
            mStoreWebsiteTextInputLayout.setErrorEnabled(true);
            return result;
        } else if (!Patterns.WEB_URL.matcher(website).matches()) {
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

        mStoreNameTextInputLayout.setErrorEnabled(false);
        mStoreAreaTextInputLayout.setErrorEnabled(false);
        mStoreWebsiteTextInputLayout.setErrorEnabled(false);
        mStoreCountersTextInputLayout.setErrorEnabled(false);

        return true;
    }
}
