package in.mobifirst.tagtree.tokens;

import java.util.List;

import in.mobifirst.tagtree.model.Token;
import in.mobifirst.tagtree.mvp.BasePresenter;
import in.mobifirst.tagtree.mvp.BaseView;

public interface TokensContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showTokens(List<Token> tokens);

        void showLoadingTokensError();

        void showNoTokens();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilterLabel();

        void showNoActiveTokens();

        void showNoCompletedTokens();

        boolean isActive();

        void showFilteringPopUpMenu();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadTokens(boolean forceUpdate);

        void setFiltering(TokensFilterType requestType);

        TokensFilterType getFiltering();
    }
}
