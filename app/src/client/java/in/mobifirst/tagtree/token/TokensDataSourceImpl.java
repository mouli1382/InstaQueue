package in.mobifirst.tagtree.token;

import android.support.annotation.NonNull;

import java.util.List;

import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.util.ApplicationConstants;
import rx.Observable;

public class TokensDataSourceImpl implements TokensDataSource {

    private FirebaseDatabaseManager mFirebaseDatabaseManager;
    private IQSharedPreferences mSharedPreferences;

    public TokensDataSourceImpl(FirebaseDatabaseManager firebaseDatabaseManager, IQSharedPreferences iqSharedPreferences) {
        mFirebaseDatabaseManager = firebaseDatabaseManager;
        this.mSharedPreferences = iqSharedPreferences;
    }

    @Override
    public Observable<List<Token>> getTokens() {
        return mFirebaseDatabaseManager.getAllTokens();
    }

    @Override
    public Observable<Token> getToken(@NonNull String tokenId) {
        return mFirebaseDatabaseManager.getTokenById(tokenId);
    }

    @Override
    public void refreshTokens() {
    }
}
