package in.gm.instaqueue.tokens;

import android.support.annotation.NonNull;

import java.util.List;

import in.gm.instaqueue.mvp.BasePresenter;
import in.gm.instaqueue.mvp.BaseView;
import in.gm.instaqueue.model.Token;

public interface TokensContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showTokens(List<Token> tokens);

        void showTokenDetailsUi(String tokenId);

        void showTokenMarkedComplete();

        void showTokenMarkedActive();

        void showTokenMarkedCancel();

        void showCompletedTokensCleared();

        void showLoadingTokensError();

        void showNoTokens();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilterLabel();

        void showCancelledFilterLabel();

        void showNoActiveTokens();

        void showNoCompletedTokens();

        void showNoCancelledTokens();

        boolean isActive();

        void showFilteringPopUpMenu();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadTokens(boolean forceUpdate);

        void openTokenDetails(@NonNull Token requestedToken);

        void setFiltering(TokensFilterType requestType);

        TokensFilterType getFiltering();
    }
}
