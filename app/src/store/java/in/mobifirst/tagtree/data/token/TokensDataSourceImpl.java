package in.mobifirst.tagtree.data.token;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.tokens.Snap;
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
    public Observable<List<Snap>> getSnaps() {
        return mFirebaseDatabaseManager.getAllSnaps(mFirebaseAuth.getCurrentUser().getUid());
    }

    @Override
    public Observable<List<Token>> getTokens() {
        return mFirebaseDatabaseManager.getAllTokens(mFirebaseAuth.getCurrentUser().getUid());
    }

    @Override
    public Observable<Token> getToken(@NonNull String tokenId) {
        return mFirebaseDatabaseManager.getTokenById(tokenId);
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
}
