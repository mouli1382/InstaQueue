package in.mobifirst.tagtree.data.token;

import android.support.annotation.NonNull;

import java.util.List;

import in.mobifirst.tagtree.model.Token;
import rx.Observable;
import rx.Subscriber;

public interface TokensDataSource {

    Observable<List<Token>> getTokens();

    Observable<Token> getToken(@NonNull String tokenId);

    void addNewToken(@NonNull Token token, Subscriber<? super String> subscriber);

    void activateToken(@NonNull Token token);

    void activateToken(@NonNull String tokenId);

    void completeToken(@NonNull Token token);

    void completeToken(@NonNull String tokenId);

    void cancelToken(@NonNull Token token);

    void cancelToken(@NonNull String tokenId);

    void clearCompletedTokens();

    void refreshTokens();
}
