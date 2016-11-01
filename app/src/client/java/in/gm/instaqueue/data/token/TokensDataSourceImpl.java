package in.gm.instaqueue.data.token;

import android.support.annotation.NonNull;

import java.util.List;

import in.gm.instaqueue.database.FirebaseDatabaseManager;
import in.gm.instaqueue.model.Token;
import in.gm.instaqueue.preferences.IQSharedPreferences;
import in.gm.instaqueue.util.ApplicationConstants;
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
        return mFirebaseDatabaseManager.getAllTokens(mSharedPreferences.getSting(ApplicationConstants.PHONE_NUMBER_KEY));
    }

    @Override
    public Observable<Token> getToken(@NonNull String tokenId) {
        return mFirebaseDatabaseManager.getTokenById(tokenId);
    }

    @Override
    public void refreshTokens() {
    }
}
