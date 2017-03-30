package in.mobifirst.tagtree.view;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import in.mobifirst.tagtree.R;

public class ProgressDialogFragment extends DialogFragment {

    private static final String ARG_MESSAGE = "message";
    private static final String ARG_INDETERMINATE = "indeterminate";

    public static boolean DIALOG_INDETERMINATE = true;
    public static boolean DIALOG_NOT_INDETERMINATE;
    public static boolean DIALOG_CANCELABLE = false;

    public static ProgressDialogFragment newInstance() {
        return newInstance(R.string.create_token);
    }

    public static ProgressDialogFragment newInstance(int message) {
        return newInstance(message, DIALOG_INDETERMINATE, DIALOG_CANCELABLE);
    }

    public static ProgressDialogFragment newInstance(int message, boolean indeterminate, boolean cancelable) {
        Bundle args = new Bundle();
        args.putInt(ARG_MESSAGE, message);
        args.putBoolean(ARG_INDETERMINATE, indeterminate);

        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.setArguments(args);
        progressDialogFragment.setCancelable(cancelable);
        return progressDialogFragment;
    }

    public static ProgressDialogFragment newInstance(String message, boolean indeterminate, boolean cancelable) {
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        args.putBoolean(ARG_INDETERMINATE, indeterminate);

        ProgressDialogFragment progressDialogFragment = new ProgressDialogFragment();
        progressDialogFragment.setArguments(args);
        progressDialogFragment.setCancelable(cancelable);
        return progressDialogFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        String message = arguments.getString(ARG_MESSAGE, null);
        if (message == null) {
            message = getString(arguments.getInt(ARG_MESSAGE));
            if (message == null) {
                message = getString(R.string.loading);
            }
        }
        boolean indeterminate = arguments.getBoolean(ARG_INDETERMINATE, DIALOG_NOT_INDETERMINATE);

        ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(message);
        progressDialog.setIndeterminate(indeterminate);
        return progressDialog;
    }

}