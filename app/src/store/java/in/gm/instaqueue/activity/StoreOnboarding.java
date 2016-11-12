package in.gm.instaqueue.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;


import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import in.gm.instaqueue.R;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Named;

import in.gm.instaqueue.R;
import in.gm.instaqueue.application.IQStoreApplication;
import in.gm.instaqueue.authentication.FirebaseAuthenticationManager;
import in.gm.instaqueue.database.FirebaseDatabaseManager;
import in.gm.instaqueue.preferences.IQSharedPreferences;
import in.gm.instaqueue.tokens.TokensActivity;
import in.gm.instaqueue.util.ApplicationConstants;

public class StoreOnboarding extends BaseActivity {

    private static final int PICK_IMAGE_REQUEST = 9002;

    @Inject
    @Named("gauth")
    FirebaseAuthenticationManager mAuthenticationManager;


    @Inject
    public FirebaseDatabaseManager mDatabaseManager;


    FirebaseStorage mStorage;
    private String mStoreName;
    private String mStoreAddress;
    private String mStorePicUrl;

    public static void start(Context caller) {
        Intent intent = new Intent(caller, StoreOnboarding.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        caller.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((IQStoreApplication)getApplication())
                .getApplicationComponent()
                .inject(this);

        setContentView(R.layout.activity_store_onboarding);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mStorage =  FirebaseStorage.getInstance();

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.INVISIBLE);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();


            }
        });
        Button uploadButton = (Button)  findViewById(R.id.uploadButton);
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

        Button saveButton = (Button)  findViewById(R.id.saveButton);
        EditText nameEdit = (EditText)  findViewById(R.id.nameEdit);
        EditText addressEdit = (EditText)  findViewById(R.id.addressEdit);
        mStoreName= nameEdit.getText().toString();
        mStoreAddress = addressEdit.getText().toString();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatabaseManager.getDatabaseReference().child("store").child(mAuthenticationManager.getAuthInstance().getCurrentUser().getUid()).child("Name").setValue(mStoreName);
                mDatabaseManager.getDatabaseReference().child("store").child(mAuthenticationManager.getAuthInstance().getCurrentUser().getUid()).child("StoreAddress").setValue(mStoreAddress);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                ImageView imageView = (ImageView) findViewById(R.id.imageView2);
                imageView.setImageBitmap(bitmap);

                // Create a storage reference from our app
                StorageReference storageRef = mStorage.getReferenceFromUrl("gs://instaqueue-9f086.appspot.com");

// Create a reference to "mountains.jpg"
                StorageReference picRef = storageRef.child(mAuthenticationManager.getAuthInstance().getCurrentUser().getUid()).child("storeProfilePic.jpg");

                // Get the data from an ImageView as bytes
                imageView.setDrawingCacheEnabled(true);
                imageView.buildDrawingCache();
                imageView.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] bitdata = baos.toByteArray();

                UploadTask uploadTask = picRef.putBytes(bitdata);
                ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
                progressBar.setVisibility(View.VISIBLE);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);

                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar2);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
