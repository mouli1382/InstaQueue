package in.mobifirst.tagtree.notokens;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import in.mobifirst.tagtree.R;

public class FirstLaunchDialogFragment extends DialogFragment {

    public interface IDialogClosedListener {
        void onDialogClosed();
    }


    public FirstLaunchDialogFragment() {
        // Required empty public constructor
    }

    public static FirstLaunchDialogFragment newInstance(Bundle args) {
        FirstLaunchDialogFragment frag = new FirstLaunchDialogFragment();
        frag.setArguments(args);
        return frag;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_fragment_first_launch, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setCancelable(false);

        ImageButton close = (ImageButton) view.findViewById(R.id.btnCancel);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IDialogClosedListener) getActivity()).onDialogClosed();
                dismiss();
            }
        });
    }
}
