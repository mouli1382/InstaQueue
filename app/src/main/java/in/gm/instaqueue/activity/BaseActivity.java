package in.gm.instaqueue.activity;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class BaseActivity extends AppCompatActivity {

//    private ProgressDialog mProgressDialog;
//
//    public void showProgressDialog() {
//        if (mProgressDialog == null) {
//            mProgressDialog = new ProgressDialog(this);
//            mProgressDialog.setCancelable(false);
//            mProgressDialog.setMessage("Loading...");
//        }
//
//        mProgressDialog.show();
//    }
//
//    public void hideProgressDialog() {
//        if (mProgressDialog != null && mProgressDialog.isShowing()) {
//            mProgressDialog.dismiss();
//        }
//    }

    public void showMessage(View view, int resId) {
        Snackbar snackbar = Snackbar
                .make(view, getString(resId), Snackbar.LENGTH_LONG);

        snackbar.show();
    }

    public void showMessage(View view, String message) {
        Snackbar snackbar = Snackbar
                .make(view, message, Snackbar.LENGTH_LONG);

        snackbar.show();
    }
}
