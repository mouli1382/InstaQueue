package in.mobifirst.tagtree.data.token;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.tokens.Snap;
import rx.Observable;
import rx.Subscriber;

@Singleton
public class TokensRepository implements TokensDataSource {

    private final TokensDataSource mTokensDataSource;

    @Inject
    TokensRepository(TokensDataSource tokensDataSource) {
        mTokensDataSource = tokensDataSource;
    }

    @Override
    public Observable<List<Token>> getTokens(String serviceUid, int mCurrentCounter) {
        return mTokensDataSource.getTokens(serviceUid, mCurrentCounter);
    }

    @Override
    public Observable<List<Snap>> getSnaps(String serviceUid, long date, boolean ascending) {
        return mTokensDataSource.getSnaps(serviceUid, date, ascending);
    }

    @Override
    public Observable<Token> getToken(String serviceUid, long date, @NonNull String tokenId) {
        return mTokensDataSource.getToken(serviceUid, date, tokenId);
    }

    @Override
    public void addNewToken(@NonNull Token token, Subscriber<? super String> subscriber) {
        mTokensDataSource.addNewToken(token, subscriber);
    }

    @Override
    public void activateToken(@NonNull Token token) {
        mTokensDataSource.activateToken(token);
    }

    @Override
    public void activateToken(@NonNull String tokenId) {

    }

    @Override
    public void completeToken(@NonNull Token token) {
        mTokensDataSource.completeToken(token);
    }

    @Override
    public void completeToken(@NonNull String tokenId) {

    }

    @Override
    public void cancelToken(@NonNull Token token) {
        mTokensDataSource.cancelToken(token);
    }

    @Override
    public void cancelToken(@NonNull String tokenId) {

    }

    @Override
    public void clearCompletedTokens() {

    }

    @Override
    public void refreshTokens() {

    }

    @Override
    public Task<Boolean> createAppointmentSlots(String serviceUid, long date) {
        return mTokensDataSource.createAppointmentSlots(serviceUid, date);
    }
}
