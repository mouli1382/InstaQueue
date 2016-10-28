package in.gm.instaqueue.data.token;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import in.gm.instaqueue.model.Token;

@Singleton
public class TokensRepository implements TokensDataSource {

    private final TokensDataSource mTokensDataSource;

    @Inject
    TokensRepository(TokensDataSource tokensDataSource) {
        mTokensDataSource = tokensDataSource;
    }

    @Override
    public void getTokens(@NonNull LoadTokensCallback callback) {

    }

    @Override
    public void getToken(@NonNull String TokenId, @NonNull GetTokenCallback callback) {

    }

    @Override
    public void saveToken(@NonNull Token Token) {

    }

    @Override
    public void activateToken(@NonNull Token Token) {

    }

    @Override
    public void activateToken(@NonNull String TokenId) {

    }

    @Override
    public void completeToken(@NonNull Token Token) {

    }

    @Override
    public void completeToken(@NonNull String TokenId) {

    }

    @Override
    public void cancelToken(@NonNull Token Token) {

    }

    @Override
    public void cancelToken(@NonNull String TokenId) {

    }

    @Override
    public void clearCompletedTokens() {

    }

    @Override
    public void refreshTokens() {

    }

    @Override
    public void deleteAllTokens() {

    }

    @Override
    public void deleteToken(@NonNull String TokenId) {

    }
}
