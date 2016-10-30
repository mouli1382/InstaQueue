package in.gm.instaqueue.addedittoken;


import in.gm.instaqueue.mvp.BasePresenter;
import in.gm.instaqueue.mvp.BaseView;

public interface AddEditTokenContract {

    interface View extends BaseView<Presenter> {

        void showEmptyTokenError();

        void showTokensList();

//        void setTitle(String title);

//        void setDescription(String description);

        boolean isActive();
    }

    interface Presenter extends BasePresenter {

        void addNewToken(String phoneNumber);

//        void populateToken();
    }
}
