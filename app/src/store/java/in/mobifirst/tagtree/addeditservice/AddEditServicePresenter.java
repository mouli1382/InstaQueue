package in.mobifirst.tagtree.addeditservice;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import javax.inject.Inject;

import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.model.Service;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class AddEditServicePresenter implements AddEditServiceContract.Presenter {

    @NonNull
    private final FirebaseDatabaseManager mFirebaseDatabaseManager;

    @NonNull
    private final FirebaseAuthenticationManager mFirebaseAuthenticationManager;

    @NonNull
    private final AddEditServiceContract.View mSettingsView;

    @Nullable
    private String mServiceId;

    @Inject
    AddEditServicePresenter(@Nullable String serviceId, FirebaseDatabaseManager firebaseDatabaseManager,
                            FirebaseAuthenticationManager firebaseAuthenticationManager, AddEditServiceContract.View settingsView) {
        mServiceId = serviceId;
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
        if (!TextUtils.isEmpty(mServiceId)) {
            getServiceDetails();
        }
    }

    @Override
    public void unsubscribe() {
//        if (mSubscriptions != null) {
//            mSubscriptions.clear();
//        }
    }

    @Override
    public void addServiceDetails(final Service service) {
        if (service.isEmpty()) {
            mSettingsView.showEmptyServiceError();
        } else {
            service.setStoreId(mFirebaseAuthenticationManager
                    .getAuthInstance().getCurrentUser().getUid());
            mFirebaseDatabaseManager.addNewService(service, new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    mSettingsView.showServicesList(service);
                }

                @Override
                public void onError(Throwable e) {
                    if (mSettingsView.isActive()) {
                        mSettingsView.showAddServiceFailedError();
                    }
                }

                @Override
                public void onNext(String result) {
                }
            });
        }
    }

    @Override
    public void editServiceDetails(final Service service) {
        if (service.isEmpty()) {
            mSettingsView.showEmptyServiceError();
        } else {
            service.setStoreId(mFirebaseAuthenticationManager
                    .getAuthInstance().getCurrentUser().getUid());
            mFirebaseDatabaseManager.editService(service, new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    mSettingsView.showServicesList(service);
                }

                @Override
                public void onError(Throwable e) {
                    if (mSettingsView.isActive()) {
                        mSettingsView.showEditServiceFailedError();
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
        mFirebaseDatabaseManager.getServiceById(mFirebaseAuthenticationManager
                .getAuthInstance().getCurrentUser().getUid(), mServiceId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Service>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mSettingsView.isActive()) {
                            mSettingsView.showEmptyServiceError();
                        }
                    }

                    @Override
                    public void onNext(Service service) {
                        if (service != null) {
                            mSettingsView.populateService(service);
                        }
                    }
                });
    }
}
