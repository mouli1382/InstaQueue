package in.mobifirst.tagtree.addeditservice;

import android.net.Uri;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.model.Service;
import in.mobifirst.tagtree.model.Store;
import in.mobifirst.tagtree.storage.FirebaseStorageManager;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

public class AddEditServicePresenter implements AddEditServiceContract.Presenter {

    @NonNull
    private final FirebaseDatabaseManager mFirebaseDatabaseManager;

    @NonNull
    private final FirebaseStorageManager mFirebaseStorageManager;

    @NonNull
    private final FirebaseAuthenticationManager mFirebaseAuthenticationManager;

    @NonNull
    private final AddEditServiceContract.View mSettingsView;

    @NonNull
    private CompositeSubscription mSubscriptions;

    @Inject
    AddEditServicePresenter(FirebaseStorageManager firebaseStorageManager, FirebaseDatabaseManager firebaseDatabaseManager,
                            FirebaseAuthenticationManager firebaseAuthenticationManager, AddEditServiceContract.View settingsView) {
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
        getServiceDetails();
    }

    @Override
    public void unsubscribe() {
        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }

    @Override
    public void addServiceDetails(Service service) {
        if (service.isEmpty()) {
            mSettingsView.showEmptyServiceError();
        } else {
            mFirebaseDatabaseManager.addStore(mFirebaseAuthenticationManager
                    .getAuthInstance().getCurrentUser().getUid(), store, new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    mSettingsView.showTokensList(store);
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

    @Override
    public void getServiceDetails() {
        mFirebaseDatabaseManager.getStoreById(mFirebaseAuthenticationManager
                        .getAuthInstance().getCurrentUser().getUid(),
                new Subscriber<Store>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mSettingsView.isActive()) {
                            mSettingsView.showAddStoreFailedError();
                        }
                    }

                    @Override
                    public void onNext(Store result) {
                        if (result != null) {
                            mSettingsView.populateStore(result);
                        }
                    }
                });
    }
}
