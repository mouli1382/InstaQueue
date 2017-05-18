package in.mobifirst.tagtree.addeditstore;


import android.net.Uri;

import in.mobifirst.tagtree.model.Store;
import in.mobifirst.tagtree.mvp.BasePresenter;
import in.mobifirst.tagtree.mvp.BaseView;

public interface AddEditStoreContract {

    interface View extends BaseView<Presenter> {

        void showUploadFailedError();

        void onFileUploadFinished(Uri uri);

        void showEmptyStoreError();

        void showAddStoreFailedError();

        void showAddService(Store store);

        boolean isActive();

        void populateStore(Store store);
    }

    interface Presenter extends BasePresenter {
        void uploadFile(byte[] bitmapData);

        void addStoreDetails(Store store);

        void getStoreDetails();
    }
}
