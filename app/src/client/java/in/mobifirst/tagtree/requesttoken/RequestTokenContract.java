package in.mobifirst.tagtree.requesttoken;


import java.util.List;

import in.mobifirst.tagtree.model.Store;
import in.mobifirst.tagtree.mvp.BasePresenter;
import in.mobifirst.tagtree.mvp.BaseView;

public interface RequestTokenContract {

    interface View extends BaseView<Presenter> {

        void showEmptyStoresError();

        void setLoadingIndicator(boolean active);

        void populateStores(List<Store> storeList);

        void showTokensList();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void addNewToken(Store store);

        void fetchStores();
    }
}
