package in.gm.instaqueue.tokens;

import android.support.annotation.NonNull;

import java.util.List;

import in.gm.instaqueue.BasePresenter;
import in.gm.instaqueue.BaseView;
import in.gm.instaqueue.model.Token;

public interface TokensContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showTokens(List<Token> tokens);

        void showAddToken();

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

        void showSuccessfullySavedMessage();

        boolean isActive();

        void showFilteringPopUpMenu();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadTokens(boolean forceUpdate);

        void addNewToken();

        void openTokenDetails(@NonNull Token requestedToken);

        void activateToken(@NonNull Token activeToken);

        void completeToken(@NonNull Token completedToken);

        void cancelToken(@NonNull Token activeToken);

        void clearCompletedTokens();

        void setFiltering(TokensFilterType requestType);

        TokensFilterType getFiltering();
    }
}
