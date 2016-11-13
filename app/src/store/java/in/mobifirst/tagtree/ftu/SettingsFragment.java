package in.mobifirst.tagtree.ftu;

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
import android.text.TextUtils;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.fragment.BaseFragment;
import in.mobifirst.tagtree.model.Store;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.tokens.TokensActivity;
import in.mobifirst.tagtree.util.ApplicationConstants;

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
    private IQSharedPreferences iqSharedPreferences;


    public static SettingsFragment newInstance() {
        return new SettingsFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(@NonNull SettingsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_done);
        fab.setEnabled(false);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    Store store = new Store(mStoreNameEditText.getText().toString(),
                            mStoreAreaEditText.getText().toString(),
                            mWebsiteEditText.getText().toString(),
                            mProfilePicUri, Integer.parseInt(mCountersEditText.getText().toString()), 0);
                    mPresenter.addStoreDetails(store);
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
        mStoreNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    CharSequence storeName = mStoreNameEditText.getText();
                    if (TextUtils.isEmpty(storeName)) {
                        mStoreNameTextInputLayout.setError(getString(R.string.empty_store_name));
                    } else {
                        mStoreNameTextInputLayout.setError("");
                    }
                    return true;
                }
                return false;
            }
        });
        mStoreAreaEditText = (TextInputEditText) root.findViewById(R.id.storeArea);
        mStoreAreaEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    CharSequence storeArea = mStoreAreaEditText.getText();
                    if (TextUtils.isEmpty(storeArea)) {
                        mStoreAreaTextInputLayout.setError(getString(R.string.empty_store_area));
                    } else {
                        mStoreAreaTextInputLayout.setError("");
                    }
                }
                return true;
            }
        });
        mWebsiteEditText = (TextInputEditText) root.findViewById(R.id.website);
        mWebsiteEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    CharSequence website = mWebsiteEditText.getText();
                    if (TextUtils.isEmpty(website)) {
                        mStoreWebsiteTextInputLayout.setError(getString(R.string.empty_store_website));
                    } else if (!Patterns.WEB_URL.matcher(website).matches()) {
                        mStoreWebsiteTextInputLayout.setError(getString(R.string.invalid_store_website));
                    } else {
                        mStoreWebsiteTextInputLayout.setError("");
                    }
                }
                return true;
            }
        });

        mCountersEditText = (TextInputEditText) root.findViewById(R.id.counters);
        mCountersEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
                    CharSequence counters = mCountersEditText.getText();
                    if (TextUtils.isEmpty(counters)) {
                        mStoreCountersTextInputLayout.setError(getString(R.string.empty_store_counters));
                    } else {
                        int counterValue = Integer.parseInt(counters.toString());
                        if (counterValue > 0 && counterValue < 100) {
                            mStoreCountersTextInputLayout.setError("");
                        } else {
                            mStoreCountersTextInputLayout.setError(getString(R.string.invalid_store_counters));
                        }
                    }
                }
                return true;
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


        //Get and load the store profile pic
        //ToDo inject sharedprefs
        iqSharedPreferences = ((IQStoreApplication) getActivity().getApplicationContext()).getApplicationComponent().getIQSharedPreferences();
        mProfilePicUri = iqSharedPreferences.getSting(ApplicationConstants.PROFILE_PIC_URL_KEY);

        if (!TextUtils.isEmpty(mProfilePicUri)) {
            mStoreImageView.setEnabled(false);
            Glide.with(getActivity())
                    .load(mProfilePicUri)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            mStoreImageView.setEnabled(true);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            mStoreImageView.setEnabled(true);
                            return false;
                        }
                    })
                    .into(mStoreImageView);
        }
        mStoreImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        setRetainInstance(true);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);

                mStoreImageView.setImageBitmap(bitmap);

                // Get the data from an ImageView as bytes
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
        fab.setEnabled(true);
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
    public void showTokensList() {
        //todo: Find a better way to avoid crash
        if (getActivity() == null)
            return;

        iqSharedPreferences.putBoolean(ApplicationConstants.FTU_COMPLETED_KEY, true);

        TokensActivity.start(getActivity());
        getActivity().finish();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }


    private boolean validateInput() {
        if ((mStoreNameTextInputLayout.getError() != null
                && mStoreNameTextInputLayout.getError().length() != 0))
            return false;

        if ((mStoreAreaTextInputLayout.getError() != null
                && mStoreAreaTextInputLayout.getError().length() != 0))
            return false;

        if ((mStoreWebsiteTextInputLayout.getError() != null
                && mStoreWebsiteTextInputLayout.getError().length() != 0))
            return false;

        if ((mStoreCountersTextInputLayout.getError() != null
                && mStoreCountersTextInputLayout.getError().length() != 0))
            return false;

        return true;
    }
}
