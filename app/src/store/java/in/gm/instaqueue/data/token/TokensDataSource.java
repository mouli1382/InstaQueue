package in.gm.instaqueue.data.token;

import android.support.annotation.NonNull;

import java.util.List;

import in.gm.instaqueue.model.Token;
import rx.Observable;

public interface TokensDataSource {

    Observable<List<Token>> getTokens();

    Observable<Token> getToken(@NonNull String tokenId);

    void addNewToken(@NonNull Token token);

    void activateToken(@NonNull Token token);

    void activateToken(@NonNull String tokenId);

    void completeToken(@NonNull Token token);

    void completeToken(@NonNull String tokenId);

    void cancelToken(@NonNull Token token);

    void cancelToken(@NonNull String tokenId);

    void clearCompletedTokens();

    void refreshTokens();
}
