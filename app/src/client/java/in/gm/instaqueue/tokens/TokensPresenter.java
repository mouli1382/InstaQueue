package in.gm.instaqueue.tokens;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;

import in.gm.instaqueue.model.Token;
import in.gm.instaqueue.token.TokensRepository;


final class TokensPresenter implements TokensContract.Presenter {

    private final TokensRepository mTokensRepository;

    private final TokensContract.View mTokensView;

    private TokensFilterType mCurrentFiltering = TokensFilterType.ALL_TOKENS;

    private boolean mFirstLoad = true;

    @Inject
    TokensPresenter(TokensRepository tokensRepository, TokensContract.View tokensView) {
        mTokensRepository = tokensRepository;
        mTokensView = tokensView;
    }

    @Inject
    void setupListeners() {
        mTokensView.setPresenter(this);
    }

    @Override
    public void start() {
        loadTokens(false);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        // If a Token was successfully added, show snackbar
//        if (AddEditTokenActivity.REQUEST_ADD_Token == requestCode
//                && Activity.RESULT_OK == resultCode) {
//            mTokensView.showSuccessfullySavedMessage();
//        }
    }

    @Override
    public void loadTokens(boolean forceUpdate) {
        // Simplification for sample: a network reload will be forced on first load.
        loadTokens(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link in.gm.instaqueue.data.token.TokensDataSource}
     * @param showLoadingUI Pass in true to display a loading icon in the UI
     */
    private void loadTokens(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mTokensView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mTokensRepository.refreshTokens();
        }

//        mTokensRepository.getTokens(new TokensDataSource.LoadTokensCallback() {
//            @Override
//            public void onTokensLoaded(List<Token> Tokens) {
//                List<Token> TokensToShow = new ArrayList<Token>();
//
//                // This callback may be called twice, once for the cache and once for loading
//                // the data from the server API, so we check before decrementing, otherwise
//                // it throws "Counter has been corrupted!" exception.
//                if (!EspressoIdlingResource.getIdlingResource().isIdleNow()) {
//                    EspressoIdlingResource.decrement(); // Set app as idle.
//                }
//
//                // We filter the Tokens based on the requestType
//                for (Token Token : Tokens) {
//                    switch (mCurrentFiltering) {
//                        case ALL_TokenS:
//                            TokensToShow.add(Token);
//                            break;
//                        case ACTIVE_TokenS:
//                            if (Token.isActive()) {
//                                TokensToShow.add(Token);
//                            }
//                            break;
//                        case COMPLETED_TokenS:
//                            if (Token.isCompleted()) {
//                                TokensToShow.add(Token);
//                            }
//                            break;
//                        default:
//                            TokensToShow.add(Token);
//                            break;
//                    }
//                }
//                // The view may not be able to handle UI updates anymore
//                if (!mTokensView.isActive()) {
//                    return;
//                }
//                if (showLoadingUI) {
//                    mTokensView.setLoadingIndicator(false);
//                }
//
//                processTokens(TokensToShow);
//            }
//
//            @Override
//            public void onDataNotAvailable() {
//                // The view may not be able to handle UI updates anymore
//                if (!mTokensView.isActive()) {
//                    return;
//                }
//                mTokensView.showLoadingTokensError();
//            }
//        });
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
