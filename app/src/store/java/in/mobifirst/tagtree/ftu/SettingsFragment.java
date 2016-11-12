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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.fragment.BaseFragment;
import in.mobifirst.tagtree.model.Store;
import in.mobifirst.tagtree.tokens.TokensActivity;

import static android.app.Activity.RESULT_OK;


public class SettingsFragment extends BaseFragment implements SettingsContract.View {

    private static final int PICK_IMAGE_REQUEST = 9002;
    private SettingsContract.Presenter mPresenter;

    private String mStoreName;
    private String mStoreAddress;
    private ImageView mStorePic;

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

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Store store = new Store(mStoreName, mStoreAddress, null, 0);
                mPresenter.addStoreDetails(store);
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.content_store_onboarding, container, false);

        ProgressBar progressBar = (ProgressBar) root.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);

        Button uploadButton = (Button) root.findViewById(R.id.uploadButton);
        uploadButton.setOnClickListener(new View.OnClickListener() {
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

        EditText nameEdit = (EditText) root.findViewById(R.id.nameEdit);
        EditText addressEdit = (EditText) root.findViewById(R.id.addressEdit);
        mStoreName = nameEdit.getText().toString();
        mStoreAddress = addressEdit.getText().toString();

        mStorePic = (ImageView) root.findViewById(R.id.imageView2);

        setHasOptionsMenu(true);
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
                byte[] bitdata = baos.toByteArray();

                mPresenter.result(requestCode, resultCode, bitdata);
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
