package in.mobifirst.tagtree.tokens;

import android.app.Activity;
import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.data.token.TokensDataSource;
import in.mobifirst.tagtree.addedittoken.AddEditTokenActivity;
import in.mobifirst.tagtree.data.token.TokensRepository;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


final class TokensPresenter implements TokensContract.Presenter {

    private final TokensRepository mTokensRepository;

    private final TokensContract.View mTokensView;

    private TokensFilterType mCurrentFiltering = TokensFilterType.ALL_TOKENS;

    private boolean mFirstLoad = true;

    private CompositeSubscription mSubscriptions;

    @Inject
    TokensPresenter(TokensRepository tokensRepository, TokensContract.View tokensView) {
        mTokensRepository = tokensRepository;
        mTokensView = tokensView;
        mSubscriptions = new CompositeSubscription();
    }

    @Inject
    void setupListeners() {
        mTokensView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadTokens(false);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // If a Token was successfully added, show snackbar
        if (AddEditTokenActivity.REQUEST_ADD_TOKEN == requestCode
                && Activity.RESULT_OK == resultCode) {
            mTokensView.showSuccessfullySavedMessage();
        }
    }

    @Override
    public void loadTokens(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadTokens(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link TokensDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
//    private void loadTokens(boolean forceUpdate, final boolean showLoadingUI) {
//        if (showLoadingUI) {
//            mTokensView.setLoadingIndicator(true);
//        }
//        if (forceUpdate) {
//            mTokensRepository.refreshTokens();
//        }
//
//        mSubscriptions.clear();
//        Subscription subscription = mTokensRepository
//                .getTokens()
//                .flatMap(new Func1<List<Token>, Observable<Token>>() {
//                    @Override
//                    public Observable<Token> call(List<Token> tokens) {
//                        return Observable.from(tokens);
//                    }
//                })
//                .filter(new Func1<Token, Boolean>() {
//                    @Override
//                    public Boolean call(Token token) {
//                        switch (mCurrentFiltering) {
//                            case ACTIVE_TOKENS:
//                                return token.isActive();
//                            case COMPLETED_TOKENS:
//                                return token.isCompleted();
//                            case CANCELLED_TOKENS:
//                                return token.isCancelled();
//                            default:
//                                return true;
//                        }
//                    }
//                })
//                .toList()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<List<Token>>() {
//                    @Override
//                    public void onCompleted() {
//                        mTokensView.setLoadingIndicator(false);
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        mTokensView.showLoadingTokensError();
//                    }
//
//                    @Override
//                    public void onNext(List<Token> tokens) {
//                        processTokens(tokens);
//                    }
//                });
//        mSubscriptions.add(subscription);
//    }

    private void loadTokens(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mTokensView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mTokensRepository.refreshTokens();
        }

        mSubscriptions.clear();
        Subscription subscription = mTokensRepository
                .getTokens()
                .flatMap(new Func1<List<Token>, Observable<Token>>() {
                    @Override
                    public Observable<Token> call(List<Token> tokens) {
                        return Observable.from(tokens);
                    }
                })
                .filter(new Func1<Token, Boolean>() {
                    @Override
                    public Boolean call(Token token) {
//                        switch (mCurrentFiltering) {
//                            case ACTIVE_TOKENS:
                                return token.isActive();
//                            case COMPLETED_TOKENS:
//                                return token.isCompleted();
//                            case CANCELLED_TOKENS:
//                                return token.isCancelled();
//                            default:
//                                return true;
//                        }
                    }
                })
                .toMultimap(new Func1<Token, Integer>() {
                    @Override
                    public Integer call(Token token) {
                        return token.getCounter();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Map<Integer, Collection<Token>>>() {
                    @Override
                    public void onCompleted() {
                        mTokensView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mTokensView.showLoadingTokensError();
                    }

                    @Override
                    public void onNext(Map<Integer, Collection<Token>> integerTokenMap) {
                        //Pass it to the TokensFragment
                        processTokens(integerTokenMap);
                    }
                });
        mSubscriptions.add(subscription);
    }

    private void processTokens(Map<Integer, Collection<Token>> tokenMap) {
        if (tokenMap == null || tokenMap.size() == 0) {
            // Show a message indicating there are no Tokens for that filter type.
            processEmptyTokens();
        } else {
            // Show the list of Tokens
            mTokensView.showTokens(tokenMap);
            // Set the filter label's text.
            showFilterLabel();
        }
    }


    private void processTokens(List<Token> Tokens) {
        if (Tokens.isEmpty()) {
            // Show a message indicating there are no Tokens for that filter type.
            processEmptyTokens();
        } else {
            // Show the list of Tokens
            mTokensView.showTokens(Tokens);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_TOKENS:
                mTokensView.showActiveFilterLabel();
                break;
            case COMPLETED_TOKENS:
                mTokensView.showCompletedFilterLabel();
                break;
            case CANCELLED_TOKENS:
                mTokensView.showCancelledFilterLabel();
                break;
            default:
                mTokensView.showAllFilterLabel();
                break;
        }
    }

    private void processEmptyTokens() {
        switch (mCurrentFiltering) {
            case ACTIVE_TOKENS:
                mTokensView.showNoActiveTokens();
                break;
            case COMPLETED_TOKENS:
                mTokensView.showNoCompletedTokens();
                break;
            case CANCELLED_TOKENS:
                mTokensView.showNoCancelledTokens();
                break;
            default:
                mTokensView.showNoTokens();
                break;
        }
    }

    @Override
    public void addNewToken() {
        mTokensView.showAddToken();
    }

    @Override
    public void openTokenDetails(@NonNull Token requestedToken) {
//        mTokensView.showTokenDetailsUi(requestedToken.getId());
    }

    @Override
    public void completeToken(@NonNull Token completedToken) {
        mTokensRepository.completeToken(completedToken);
        mTokensView.showTokenMarkedComplete();
        loadTokens(false, false);
    }

    @Override
    public void activateToken(@NonNull Token activeToken) {
        mTokensRepository.activateToken(activeToken);
        mTokensView.showTokenMarkedActive();
        loadTokens(false, false);
    }

    @Override
    public void cancelToken(@NonNull Token activeToken) {
        mTokensRepository.activateToken(activeToken);
        mTokensView.showTokenMarkedCancel();
        loadTokens(false, false);
    }

    @Override
    public void clearCompletedTokens() {
        mTokensRepository.clearCompletedTokens();
        mTokensView.showCompletedTokensCleared();
        loadTokens(false, false);
    }

    @Override
    public void setFiltering(TokensFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    @Override
    public TokensFilterType getFiltering() {
        return mCurrentFiltering;
    }

}
