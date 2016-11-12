package in.mobifirst.tagtree.ftu;


import in.mobifirst.tagtree.model.Store;
import in.mobifirst.tagtree.mvp.BasePresenter;
import in.mobifirst.tagtree.mvp.BaseView;

public interface SettingsContract {

    interface View extends BaseView<Presenter> {

        void showUploadFailedError();

        void showEmptyStoreError();

        void showAddStoreFailedError();

        void showTokensList();

        boolean isActive();
    }

    interface Presenter extends BasePresenter {
        void result(int requestCode, int resultCode, byte[] data);

        void addStoreDetails(Store store);
    }
}
