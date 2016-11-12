package in.mobifirst.tagtree.tokens;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.token.TokensRepository;
import in.mobifirst.tagtree.model.Token;
import rx.Observable;
import rx.Observer;
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
    }

    @Override
    public void loadTokens(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadTokens(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link in.mobifirst.tagtree.token.TokensDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
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
                        switch (mCurrentFiltering) {
                            case ACTIVE_TOKENS:
                                return token.isActive();
                            case COMPLETED_TOKENS:
                                return token.isCompleted();
                            case CANCELLED_TOKENS:
                                return token.isCancelled();
                            default:
                                return true;
                        }
                    }
                })
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Token>>() {
                    @Override
                    public void onCompleted() {
                        mTokensView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mTokensView.showLoadingTokensError();
                    }

                    @Override
                    public void onNext(List<Token> tokens) {
                        processTokens(tokens);
                    }
                });
        mSubscriptions.add(subscription);
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
    public void openTokenDetails(@NonNull Token requestedToken) {
//        mTokensView.showTokenDetailsUi(requestedToken.getId());
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
