package in.mobifirst.tagtree.data.token;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.tokens.Snap;
import in.mobifirst.tagtree.util.TimeUtils;
import rx.Observable;
import rx.Subscriber;

public class TokensDataSourceImpl implements TokensDataSource {

    private FirebaseDatabaseManager mFirebaseDatabaseManager;
    private FirebaseAuth mFirebaseAuth;

    public TokensDataSourceImpl(FirebaseDatabaseManager firebaseDatabaseManager, FirebaseAuthenticationManager firebaseAuthenticationManager) {
        mFirebaseDatabaseManager = firebaseDatabaseManager;
        mFirebaseAuth = firebaseAuthenticationManager.getAuthInstance();
    }

    @Override
    public Observable<List<Token>> getTokens(String serviceUid, int currentCounter) {
        return mFirebaseDatabaseManager.getAllTokens(mFirebaseAuth.getCurrentUser().getUid(), serviceUid, currentCounter);
    }

    @Override
    public Observable<List<Snap>> getSnaps(String serviceUid, long date, boolean ascending) {
        return mFirebaseDatabaseManager.getAllSnaps(mFirebaseAuth.getCurrentUser().getUid(), serviceUid, date, ascending);
    }

    @Override
    public Observable<Token> getToken(String serviceUid, long date, @NonNull String tokenId) {
        return mFirebaseDatabaseManager.getTokenById(mFirebaseAuth.getCurrentUser().getUid(), serviceUid, date, tokenId);
    }

    @Override
    public void addNewToken(@NonNull Token token, Subscriber<? super String> subscriber) {
        token.setStoreId(mFirebaseAuth.getCurrentUser().getUid());
        mFirebaseDatabaseManager.addNewToken(token, subscriber);
    }

    @Override
    public void activateToken(@NonNull Token token) {
        token.setStatus(Token.Status.READY.ordinal());
        token.setBuzzCount(token.getBuzzCount() + 1);
        mFirebaseDatabaseManager.updateToken(token);
    }

    @Override
    public void activateToken(@NonNull String tokenId) {

    }

    @Override
    public void completeToken(@NonNull Token token) {
        token.setStatus(Token.Status.COMPLETED.ordinal());
        mFirebaseDatabaseManager.updateToken(token);
    }

    @Override
    public void completeToken(@NonNull String tokenId) {

    }

    @Override
    public void cancelToken(@NonNull Token token) {
        token.setStatus(Token.Status.CANCELLED.ordinal());
        mFirebaseDatabaseManager.updateToken(token);
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
        return mFirebaseDatabaseManager.checkAndGenerateAppointments(mFirebaseAuth.getCurrentUser().getUid(), serviceUid, TimeUtils.getDate(date));
    }
}
