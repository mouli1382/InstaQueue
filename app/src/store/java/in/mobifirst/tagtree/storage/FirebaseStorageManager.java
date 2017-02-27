package in.mobifirst.tagtree.storage;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import javax.inject.Inject;

import in.mobifirst.tagtree.BuildConfig;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import rx.Subscriber;

public class FirebaseStorageManager {
    private static final String TAG = "FirebaseStorageManager";
    private static final String FILE_NAME = "storeProfilePic.jpg";

    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private IQSharedPreferences mIQSharedPreferences;

    @Inject
    public FirebaseStorageManager(IQSharedPreferences iqSharedPreferences) {
        mIQSharedPreferences = iqSharedPreferences;
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorageReference = mFirebaseStorage.getReferenceFromUrl(BuildConfig.STORAGE_REF);
    }

    public void uploadFile(final String uid, byte[] bitmapData, final Subscriber<? super Uri> subscriber) {
        StorageReference storeProfilePicRef = mStorageReference.child(uid).child(FILE_NAME);

        UploadTask uploadTask = storeProfilePicRef.putBytes(bitmapData);
        uploadTask.addOnFailureListener(new OnFailureListener() {

            @Override
            public void onFailure(@NonNull Exception exception) {
                subscriber.onError(exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                subscriber.onNext(downloadUrl);
                subscriber.onCompleted();
            }
        });
    }
}

