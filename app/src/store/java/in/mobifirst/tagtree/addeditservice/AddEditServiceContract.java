package in.mobifirst.tagtree.addeditservice;


import in.mobifirst.tagtree.model.Service;
import in.mobifirst.tagtree.mvp.BasePresenter;
import in.mobifirst.tagtree.mvp.BaseView;

public interface AddEditServiceContract {

    interface View extends BaseView<Presenter> {

        void showEmptyServiceError();

        void showAddServiceFailedError();

        void showServicesList(Service service);

        boolean isActive();

        void populateService(Service service);
    }

    interface Presenter extends BasePresenter {
        void addServiceDetails(Service service);

        void getServiceDetails();
    }
}
