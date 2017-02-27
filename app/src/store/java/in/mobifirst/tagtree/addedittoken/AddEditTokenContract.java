package in.mobifirst.tagtree.addedittoken;


import in.mobifirst.tagtree.mvp.BasePresenter;
import in.mobifirst.tagtree.mvp.BaseView;

public interface AddEditTokenContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTokenError();

        void showTokensList();

//        void setTitle(String title);

//        void setDescription(String description);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void addNewToken(String phoneNumber, int counterNumber);

//        void populateToken();
    }
}
