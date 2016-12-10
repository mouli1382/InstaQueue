package in.mobifirst.tagtree.requesttoken;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.model.Store;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class RequestTokenPresenter implements RequestTokenContract.Presenter {


    @Inject
    protected FirebaseDatabaseManager mFirebaseDatabaseManager;

    @NonNull
    private final RequestTokenContract.View mRequestTokenView;

    @NonNull
    private CompositeSubscription mSubscriptions;

    @Inject
    RequestTokenPresenter(RequestTokenContract.View addTokenView) {
        mRequestTokenView = addTokenView;
    }

    @Inject
    void setupListeners() {
        mRequestTokenView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        fetchStores();
    }

    @Override
    public void unsubscribe() {
        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }

    @Override
    public void addNewToken(Store store) {
        mFirebaseDatabaseManager.addNewToken(store, new Subscriber<String>() {
            @Override
            public void onCompleted() {
                mRequestTokenView.showTokensList();
            }

            @Override
            public void onError(Throwable e) {
                if (mRequestTokenView.isActive()) {
                    mRequestTokenView.showEmptyStoresError();
                }
            }

            @Override
            public void onNext(String result) {
            }
        });
    }

    @Override
    public void fetchStores() {
        mRequestTokenView.setLoadingIndicator(true);

        mSubscriptions.clear();
        Subscription subscription = mFirebaseDatabaseManager
                .getAllStores()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Store>>() {
                    @Override
                    public void onCompleted() {
                        mRequestTokenView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mRequestTokenView.setLoadingIndicator(false);
                        mRequestTokenView.showEmptyStoresError();
                    }

                    @Override
                    public void onNext(List<Store> stores) {
                        mRequestTokenView.setLoadingIndicator(false);
                        mRequestTokenView.populateStores(stores);
                    }
                });
        mSubscriptions.add(subscription);
    }
}
