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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

    private String mStoreName;
    private String mStoreAddress;
    private String mStoreWebsite;
    private String mProfilePic;
    private ImageView mStorePic;
    private ProgressBar mProgressBar;
    private Button mUploadButton;
    private FloatingActionButton fab;
    TextInputEditText storeName;
    TextInputEditText storeArea;
    TextInputEditText website;

    private byte[] bitmapData;


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
                mStoreName = storeName.getText().toString();
                mStoreAddress = storeArea.getText().toString();
                mStoreWebsite = website.getText().toString();
                Store store = new Store(mStoreName, mStoreAddress, mStoreWebsite, mProfilePic, 0);
                mPresenter.addStoreDetails(store);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_onboarding, container, false);

        storeName = (TextInputEditText) root.findViewById(R.id.storeName);
        storeArea = (TextInputEditText) root.findViewById(R.id.storeArea);
        website = (TextInputEditText) root.findViewById(R.id.website);

        mProgressBar = (ProgressBar) root.findViewById(R.id.logoProgress);
        mStorePic = (ImageView) root.findViewById(R.id.storeProfilePic);
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
        IQSharedPreferences iqSharedPreferences = ((IQStoreApplication) getActivity().getApplicationContext()).getApplicationComponent().getIQSharedPreferences();
        String profilePicURL = iqSharedPreferences.getSting(ApplicationConstants.PROFILE_PIC_URL_KEY);

        if (!TextUtils.isEmpty(profilePicURL)) {
            mStorePic.setEnabled(false);
            Glide.with(getActivity())
                    .load(profilePicURL)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            mStorePic.setEnabled(true);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            mProgressBar.setVisibility(View.GONE);
                            mStorePic.setEnabled(true);
                            return false;
                        }
                    })
                    .into(mStorePic);
        }
        mStorePic.setOnClickListener(new View.OnClickListener() {
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

                mStorePic.setImageBitmap(bitmap);

                // Get the data from an ImageView as bytes
                mStorePic.setDrawingCacheEnabled(true);
                mStorePic.buildDrawingCache();
                mStorePic.getDrawingCache();
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
        mProfilePic = uri.toString();
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

        TokensActivity.start(getActivity());
        getActivity().finish();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
