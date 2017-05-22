package in.mobifirst.tagtree.addedittoken;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import in.mobifirst.tagtree.data.token.TokensRepository;
import in.mobifirst.tagtree.model.Token;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class AddEditTokenPresenter implements AddEditTokenContract.Presenter {

    @NonNull
    private final TokensRepository mTokensRepository;

    @NonNull
    private final AddEditTokenContract.View mAddTokenView;

    @NonNull
    private CompositeSubscription mSubscriptions;

    @NonNull
    private Token mToken;

    @Inject
    AddEditTokenPresenter(@NonNull Token token, TokensRepository tokensRepository,
                          AddEditTokenContract.View addTokenView) {
        mToken = token;
        mTokensRepository = tokensRepository;
        mAddTokenView = addTokenView;
        mSubscriptions = new CompositeSubscription();
    }

    @Inject
    void setupListeners() {
        mAddTokenView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        if (mToken != null) {
            //ToDo - just to be sure that the slot is still available. Ideall we should be doing it at the time of booking.
            populateToken();
        }
    }

    @Override
    public void unsubscribe() {
        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }

    @Override
    public void addNewToken(String phoneNumber) {
        mAddTokenView.updateProgress(true);
        mToken.setPhoneNumber(phoneNumber);
        mToken.setStatus(Token.Status.ISSUED.ordinal());
        mToken.setBuzzCount(0);
        saveToken(mToken);
    }

    private void saveToken(@NonNull Token token) {
        if (token.isEmpty()) {
            mAddTokenView.updateProgress(false);
            mAddTokenView.showEmptyTokenError();
        } else {
            mTokensRepository.addNewToken(token, new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    mAddTokenView.updateProgress(false);
                }

                @Override
                public void onError(Throwable e) {
                    if (mAddTokenView.isActive()) {
                        mAddTokenView.updateProgress(false);
                        mAddTokenView.showEmptyTokenError();
                    }
                }

                @Override
                public void onNext(String result) {
                    mAddTokenView.updateProgress(false);
                    mAddTokenView.showTokensList(result);
                }
            });
        }
    }

    @Override
    public void populateToken() {
        Subscription subscription = mTokensRepository
                .getToken(mToken.getServiceId(), mToken.getDate(), mToken.getuId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Token>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        if (mAddTokenView.isActive()) {
                            mAddTokenView.showEmptyTokenError();
                        }
                    }

                    @Override
                    public void onNext(Token token) {
                        if (token != null) {
                            mToken = token;
                        }
                    }
                });

        mSubscriptions.add(subscription);
    }
}
