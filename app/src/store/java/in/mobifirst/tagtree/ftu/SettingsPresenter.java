package in.mobifirst.tagtree.ftu;

import android.net.Uri;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.model.Store;
import in.mobifirst.tagtree.storage.FirebaseStorageManager;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

public class SettingsPresenter implements SettingsContract.Presenter {

    @NonNull
    private final FirebaseDatabaseManager mFirebaseDatabaseManager;

    @NonNull
    private final FirebaseStorageManager mFirebaseStorageManager;

    @NonNull
    private final FirebaseAuthenticationManager mFirebaseAuthenticationManager;

    @NonNull
    private final SettingsContract.View mSettingsView;

    @NonNull
    private CompositeSubscription mSubscriptions;

    @Inject
    SettingsPresenter(FirebaseStorageManager firebaseStorageManager, FirebaseDatabaseManager firebaseDatabaseManager,
                      FirebaseAuthenticationManager firebaseAuthenticationManager, SettingsContract.View settingsView) {
        mFirebaseStorageManager = firebaseStorageManager;
        mFirebaseDatabaseManager = firebaseDatabaseManager;
        mFirebaseAuthenticationManager = firebaseAuthenticationManager;
        mSettingsView = settingsView;
    }

    @Inject
    void setupListeners() {
        mSettingsView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        showStoreDetails();
    }

    @Override
    public void unsubscribe() {
        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }

    @Override
    public void result(int requestCode, int resultCode, byte[] data) {
        uploadFile(data);
    }

    public void uploadFile(byte[] bitmapData) {
        if (bitmapData == null || bitmapData.length == 0) {
            mSettingsView.showUploadFailedError();
        } else {
            mFirebaseStorageManager.uploadFile(mFirebaseAuthenticationManager
                            .getAuthInstance().getCurrentUser().getUid(),
                    bitmapData, new Subscriber<Uri>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            mSettingsView.showUploadFailedError();
                        }

                        @Override
                        public void onNext(Uri uri) {

                        }
                    });
        }
    }

    @Override
    public void addStoreDetails(Store store) {
        if (store.isEmpty()) {
            mSettingsView.showEmptyStoreError();
        } else {
            mFirebaseDatabaseManager.addStore(store, new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    mSettingsView.showTokensList();
                }

                @Override
                public void onError(Throwable e) {
                    if (mSettingsView.isActive()) {
                        mSettingsView.showAddStoreFailedError();
                    }
                }

                @Override
                public void onNext(String result) {
                }
            });
        }
    }


    private void showStoreDetails() {

    }
}
