package in.mobifirst.tagtree.services;

import android.content.Intent;
import android.support.annotation.NonNull;

import java.util.List;

import in.mobifirst.tagtree.model.Service;
import in.mobifirst.tagtree.mvp.BasePresenter;
import in.mobifirst.tagtree.mvp.BaseView;

public interface ServicesContract {

    interface View extends BaseView<Presenter> {

        void setLoadingIndicator(boolean active);

        void showServices(List<Service> services);

        void showAddService();

        void editService(String storeUid, String serviceUid);

        void showLoadingServicesError();

        void showNoServices();

        void showServiceSavedMessage();

        boolean isActive();

        void showTokensList();
    }

    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode, Intent data);

        void loadServices();

        void addNewService();

        void openServiceDetails(@NonNull Service service);
    }
}
