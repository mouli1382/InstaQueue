package in.gm.instaqueue.token;

import android.support.annotation.NonNull;

import java.util.List;

import in.gm.instaqueue.model.Token;

public interface TokensDataSource {

    interface LoadTokensCallback {

        void onTokensLoaded(List<Token> Tokens);

        void onDataNotAvailable();
    }

    interface GetTokenCallback {

        void onTokenLoaded(Token Token);

        void onDataNotAvailable();
    }

    void getTokens(@NonNull LoadTokensCallback callback);

    void getToken(@NonNull String TokenId, @NonNull GetTokenCallback callback);

    void saveToken(@NonNull Token Token);

    void activateToken(@NonNull Token Token);

    void activateToken(@NonNull String TokenId);

    void completeToken(@NonNull Token Token);

    void completeToken(@NonNull String TokenId);

    void cancelToken(@NonNull Token Token);

    void cancelToken(@NonNull String TokenId);

    void clearCompletedTokens();

    void refreshTokens();

    void deleteAllTokens();

    void deleteToken(@NonNull String TokenId);
}
