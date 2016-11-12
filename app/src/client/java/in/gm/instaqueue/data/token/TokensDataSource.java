package in.mobifirst.tagtree.data.token;

import android.support.annotation.NonNull;

import java.util.List;

import in.mobifirst.tagtree.model.Token;
import rx.Observable;
import rx.Subscriber;

public interface TokensDataSource {

    Observable<List<Token>> getTokens();

    Observable<Token> getToken(@NonNull String tokenId);

    void refreshTokens();
}
