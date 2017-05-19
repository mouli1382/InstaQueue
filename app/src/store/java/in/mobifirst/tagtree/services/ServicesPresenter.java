package in.mobifirst.tagtree.services;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.addeditservice.AddEditServiceActivity;
import in.mobifirst.tagtree.data.service.ServicesRepository;
import in.mobifirst.tagtree.model.Service;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


final class ServicesPresenter implements ServicesContract.Presenter {
    private static final String TAG = "ServicesPresenter";

    private final ServicesRepository mServicesRepository;

    private final ServicesContract.View mServicesView;

    private boolean mFirstLoad = true;

    private CompositeSubscription mSubscriptions;

    @NonNull
    private String mStoreId;

    @Inject
    ServicesPresenter(@NonNull String storeId, ServicesRepository servicesRepository, ServicesContract.View tokensView) {
        mStoreId = storeId;
        mServicesRepository = servicesRepository;
        mServicesView = tokensView;
        mSubscriptions = new CompositeSubscription();
    }

    @Inject
    void setupListeners() {
        mServicesView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadServices();
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        // If a Service was successfully added, show snackbar
        if (AddEditServiceActivity.REQUEST_ADD_SERVICE == requestCode
                && Activity.RESULT_OK == resultCode) {
            mServicesView.showServiceSavedMessage();
        }
    }

    @Override
    public void loadServices() {
        loadServices(true);
    }

    @Override
    public void addNewService() {
        mServicesView.showAddService();
    }

    @Override
    public void openServiceDetails(@NonNull Service service) {
    }

    private void loadServices(final boolean showLoadingUI) {
        if (showLoadingUI) {
            mServicesView.setLoadingIndicator(true);
        }

        mSubscriptions.clear();
        Subscription subscription = mServicesRepository
                .getServices()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Service>>() {
                    @Override
                    public void onCompleted() {
                        mServicesView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mServicesView.setLoadingIndicator(false);
                        mServicesView.showLoadingServicesError();
                    }

                    @Override
                    public void onNext(List<Service> services) {
                        mServicesView.setLoadingIndicator(false);
                        processServices(services);
                    }
                });
        Log.e(TAG, subscription.toString());
        mSubscriptions.add(subscription);
    }

    private void processServices(List<Service> services) {
        Log.e(TAG, "processServices");
        if (services == null || services.isEmpty()) {
            mServicesView.showNoServices();
        } else {
            mServicesView.showServices(services);
        }
    }
}
