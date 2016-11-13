package in.mobifirst.tagtree.addedittoken;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.data.token.TokensRepository;
import rx.Subscriber;
import rx.subscriptions.CompositeSubscription;

public class AddEditTokenPresenter implements AddEditTokenContract.Presenter {

    @NonNull
    private final TokensRepository mTokensRepository;

    @NonNull
    private final AddEditTokenContract.View mAddTokenView;

    @Nullable
    private String mTokenId;

    @NonNull
    private CompositeSubscription mSubscriptions;

    @Inject
    AddEditTokenPresenter(@Nullable String TokenId, TokensRepository tokensRepository,
                          AddEditTokenContract.View addTokenView) {
        mTokenId = TokenId;
        mTokensRepository = tokensRepository;
        mAddTokenView = addTokenView;
    }

    @Inject
    void setupListeners() {
        mAddTokenView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        if (mTokenId != null) {
//            populateToken();
        }
    }

    @Override
    public void unsubscribe() {
        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
    }

    @Override
    public void addNewToken(String phoneNumber, int counterNumber) {
        Token token = new Token();
        token.setPhoneNumber(phoneNumber);
        token.setCounter(counterNumber);
        saveToken(token);
    }

    private void saveToken(@NonNull Token token) {
        if (token.isEmpty()) {
            mAddTokenView.showEmptyTokenError();
        } else {
            mTokensRepository.addNewToken(token, new Subscriber<String>() {
                @Override
                public void onCompleted() {
                    mAddTokenView.showTokensList();
                }

                @Override
                public void onError(Throwable e) {
                    if (mAddTokenView.isActive()) {
                        mAddTokenView.showEmptyTokenError();
                    }
                }

                @Override
                public void onNext(String result) {
                }
            });

        }
    }

//    @Override
//    public void populateToken() {
//        if (mTokenId == null) {
//            throw new RuntimeException("populateToken() was called but Token is new.");
//        }
//        Subscription subscription = mTokensRepository
//                .getToken(mTokenId)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<Token>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        if (mAddTokenView.isActive()) {
//                            mAddTokenView.showEmptyTokenError();
//                        }
//                    }
//
//                    @Override
//                    public void onNext(Token Token) {
//                        if (mAddTokenView.isActive()) {
//                            mAddTokenView.setTitle(Token.getTitle());
//                            mAddTokenView.setDescription(Token.getDescription());
//                        }
//                    }
//                });
//
//        mSubscriptions.add(subscription);
//    }
}
