package in.mobifirst.tagtree.addeditservice;

import android.support.annotation.NonNull;

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

    @Inject
    AddEditServicePresenter(FirebaseDatabaseManager firebaseDatabaseManager,
                            FirebaseAuthenticationManager firebaseAuthenticationManager, AddEditServiceContract.View settingsView) {
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
    public void getServiceDetails() {
        mFirebaseDatabaseManager.getServiceById(null)
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
