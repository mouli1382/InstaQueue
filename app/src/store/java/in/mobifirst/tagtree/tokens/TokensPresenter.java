package in.mobifirst.tagtree.tokens;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.addedittoken.AddEditTokenActivity;
import in.mobifirst.tagtree.data.token.TokensDataSource;
import in.mobifirst.tagtree.data.token.TokensRepository;
import in.mobifirst.tagtree.model.Service;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.util.ApplicationConstants;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;


final class TokensPresenter implements TokensContract.Presenter {
    private static final String TAG = "TokensPresenter";

    private final TokensRepository mTokensRepository;

    private final TokensContract.View mTokensView;

    private TokensFilterType mCurrentFiltering = TokensFilterType.ALL_TOKENS;

    private int mCurrentCounter = 1;

    private boolean mFirstLoad = true;

    private CompositeSubscription mSubscriptions;

    @Nullable
    private long mDate;

    @NonNull
    private Service mService;

    @Inject
    TokensPresenter(@NonNull Service service, @Nullable long date, TokensRepository tokensRepository, TokensContract.View tokensView) {
        mService = service;
        mDate = date;
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
        Log.e(TAG, "subscribe");
        initiateLoading();
    }

    private void initiateLoading() {
        if (mTokensView instanceof SnapFragment) {
            loadTokensMap(false);
        } else {
            loadTokens(false);
        }
    }

    @Override
    public void unsubscribe() {
        Log.e(TAG, "unsubscribe");
        mSubscriptions.clear();
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        // If a Token was successfully added, show snackbar
        if (AddEditTokenActivity.REQUEST_ADD_TOKEN == requestCode
                && Activity.RESULT_OK == resultCode) {
            String lastCreatedToken = data.getStringExtra(ApplicationConstants.LAST_CREATED_TOKEN);
            mTokensView.showSuccessfullySavedMessage(lastCreatedToken);
        }
    }

    @Override
    public void loadTokens(boolean forceUpdate) {
        Log.e(TAG, "loadTokens");
        loadTokens(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    @Override
    public void loadTokensMap(boolean forceUpdate) {
        Log.e(TAG, "loadTokensMap");
        loadTokensMap(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    /**
     * @param forceUpdate   Pass in true to refresh the data in the {@link TokensDataSource}
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
                .getTokens(mService.getId(), mCurrentCounter)
//                .flatMap(new Func1<List<Token>, Observable<Token>>() {
//                    @Override
//                    public Observable<Token> call(List<Token> tokens) {
//                        return Observable.from(tokens);
//                    }
//                })
//                .filter(new Func1<Token, Boolean>() {
//                    @Override
//                    public Boolean call(Token token) {
////                        switch (mCurrentFiltering) {
////                            case ACTIVE_TOKENS:
////                                return token.isActive();
////                            case COMPLETED_TOKENS:
////                                return token.isCompleted();
////                            case CANCELLED_TOKENS:
////                                return token.isCancelled();
////                            default:
////                                return true;
////                        }
//
//                        return !token.isCompleted();
//                    }
//                })
//                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Token>>() {
                    @Override
                    public void onCompleted() {
                        mTokensView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mTokensView.setLoadingIndicator(false);
                        mTokensView.showLoadingTokensError();
                    }

                    @Override
                    public void onNext(List<Token> tokens) {
                        mTokensView.setLoadingIndicator(false);
                        processTokens(tokens);
                    }
                });
        Log.e(TAG, subscription.toString());
        mSubscriptions.add(subscription);
    }

    private void loadTokensMap(boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mTokensView.setLoadingIndicator(true);
        }
        if (forceUpdate) {
            mTokensRepository.refreshTokens();
        }

        mSubscriptions.clear();
        Subscription subscription = mTokensRepository
                .getSnaps(mService.getId(), mDate, false)
//                .flatMap(new Func1<List<Token>, Observable<Token>>() {
//                    @Override
//                    public Observable<Token> call(List<Token> tokens) {
//                        return Observable.from(tokens);
//                    }
//                })
//                .toSortedList(new Func2<Token, Token, Integer>() {
//                    @Override
//                    public Integer call(Token token, Token token2) {
//                        return new Long(token.getTokenNumber()).compareTo(token2.getTokenNumber());
//                    }
//                })
//                .flatMap(new Func1<List<Token>, Observable<Token>>() {
//                    @Override
//                    public Observable<Token> call(List<Token> tokens) {
//                        return Observable.from(tokens);
//                    }
//                })
//                .filter(new Func1<Token, Boolean>() {
//                    @Override
//                    public Boolean call(Token token) {
////                        switch (mCurrentFiltering) {
////                            case ACTIVE_TOKENS:
////                                return token.isActive();
////                            case COMPLETED_TOKENS:
////                                return token.isCompleted();
////                            case CANCELLED_TOKENS:
////                                return token.isCancelled();
////                            default:
////                                return true;
////                        }
//
//                        return !token.isCompleted();
//                    }
//                })
//                .toMultimap(new Func1<Token, Integer>() {
//                    @Override
//                    public Integer call(Token token) {
//                        return token.getCounter();
//                    }
//                })
//                .map(new Func1<Map<Integer, Collection<Token>>, List<Snap>>() {
//                    @Override
//                    public List<Snap> call(Map<Integer, Collection<Token>> integerCollectionMap) {
//                        TreeMap<Integer, Collection<Token>> sortedMap = new TreeMap<>();
//                        sortedMap.putAll(integerCollectionMap);
//                        ArrayList<Snap> snaps = new ArrayList<>(sortedMap.size());
//                        Iterator<Integer> keyIterator = sortedMap.keySet().iterator();
//                        while (keyIterator.hasNext()) {
//                            int key = keyIterator.next();
//                            Snap snap = new Snap(key, new ArrayList<>(sortedMap.get(key)));
//                            snaps.add(snap);
//                        }
//                        return snaps;
//                    }
//                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Snap>>() {
                    @Override
                    public void onCompleted() {
                        mTokensView.setLoadingIndicator(false);
                    }

                    @Override
                    public void onError(Throwable e) {
                        mTokensView.setLoadingIndicator(false);
                        mTokensView.showLoadingTokensError();
                    }

                    @Override
                    public void onNext(List<Snap> snaps) {
                        mTokensView.setLoadingIndicator(false);
                        processSnaps(snaps);
                    }
                });
        Log.e(TAG, subscription.toString());
        mSubscriptions.add(subscription);
    }

    private void processSnaps(List<Snap> snaps) {
        Log.e(TAG, "processSnaps");
        if (snaps == null || snaps.size() == 0) {
            // Show a message indicating there are no Tokens for that filter type.
            processEmptyTokens();
        } else {
            // Show the list of Tokens
            mTokensView.showSnaps(snaps);
            // Set the filter label's text.
            showFilterLabel();
        }
    }


    private void processTokens(List<Token> tokens) {
        Log.e(TAG, "processTokens");
        if (tokens == null || tokens.isEmpty()) {
            // Show a message indicating there are no Tokens for that filter type.
            processEmptyTokens();
        } else {
            // Show the list of Tokens
            mTokensView.showTokens(tokens);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    private void showFilterLabel() {
        mTokensView.setServiceName(mService.getName());
//        switch (mCurrentFiltering) {
//            case ACTIVE_TOKENS:
//                mTokensView.showActiveFilterLabel();
//                break;
//            case COMPLETED_TOKENS:
//                mTokensView.showCompletedFilterLabel();
//                break;
//            case CANCELLED_TOKENS:
//                mTokensView.showCancelledFilterLabel();
//                break;
//            default:
//                mTokensView.showAllFilterLabel();
//                break;
//        }
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

    @Override
    public void setCounter(int counter) {
        mCurrentCounter = counter;
    }

    @Override
    public int getCounter() {
        return mCurrentCounter;
    }

    @Override
    public void createAppointmentSlots(String serviceUid) {
        mTokensView.setLoadingIndicator(true);
        mTokensRepository.createAppointmentSlots(serviceUid, mDate)
                .addOnSuccessListener(new OnSuccessListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        if (aBoolean) {
                            mTokensView.setLoadingIndicator(false);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mTokensView.setLoadingIndicator(false);
                        mTokensView.showLoadingTokensError();
                    }
                });
    }
}
