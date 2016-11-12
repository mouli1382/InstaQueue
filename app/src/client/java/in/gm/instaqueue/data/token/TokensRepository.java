package in.mobifirst.tagtree.data.token;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import in.mobifirst.tagtree.model.Token;
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
    public Observable<List<Token>> getTokens() {
        return mTokensDataSource.getTokens();
    }

    @Override
    public Observable<Token> getToken(@NonNull String tokenId) {
        return mTokensDataSource.getToken(tokenId);
    }

    @Override
    public void refreshTokens() {

    }
}
