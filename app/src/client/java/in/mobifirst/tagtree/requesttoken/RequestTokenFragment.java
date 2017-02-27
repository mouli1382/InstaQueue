package in.mobifirst.tagtree.requesttoken;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.application.IQClientApplication;
import in.mobifirst.tagtree.fragment.BaseFragment;
import in.mobifirst.tagtree.model.Store;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.util.NetworkConnectionUtils;


public class RequestTokenFragment extends BaseFragment implements RequestTokenContract.View {

    private RequestTokenContract.Presenter mPresenter;

    @Inject
    protected NetworkConnectionUtils mNetworkConnectionUtils;

    private Spinner mStoreSpinner;
    private Store selectedStore;

    public static RequestTokenFragment newInstance() {
        return new RequestTokenFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((IQClientApplication) getActivity().getApplicationContext()).getApplicationComponent()
                .inject(this);
    }

    private BroadcastReceiver mNetworkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra(TTLocalBroadcastManager.NETWORK_STATUS_KEY, false);

            if (!isConnected && getView() != null) {
                showNetworkError(getView());
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (!mNetworkConnectionUtils.isConnected()) {
            showNetworkError(getView());
        }
        TTLocalBroadcastManager.registerReceiver(getActivity(), mNetworkBroadcastReceiver, TTLocalBroadcastManager.NETWORK_INTENT_ACTION);
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        TTLocalBroadcastManager.unRegisterReceiver(getActivity(), mNetworkBroadcastReceiver);
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(@NonNull RequestTokenContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNetworkConnectionUtils.isConnected()) {
                    mPresenter.addNewToken(selectedStore);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_request_token, container, false);

        mStoreSpinner = (Spinner) root.findViewById(R.id.store_spinner);

        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mNetworkConnectionUtils.isConnected()) {
                    mPresenter.fetchStores();
                } else {
                    setLoadingIndicator(false);
                }
            }
        });


        setRetainInstance(true);
        return root;
    }

    @Override
    public void showEmptyStoresError() {
        Snackbar.make(getView(), getString(R.string.empty_token_message), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void setLoadingIndicator(final boolean active) {

        if (getView() == null) {
            return;
        }
        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);

        if (!mNetworkConnectionUtils.isConnected()) {
            // Make sure setRefreshing() is called after the layout is done with everything else.
            srl.post(new Runnable() {
                @Override
                public void run() {
                    srl.setRefreshing(false);
                }
            });
            return;
        }

        if (active) {
            if (!srl.isRefreshing()) {
                // Make sure setRefreshing() is called after the layout is done with everything else.
                srl.post(new Runnable() {
                    @Override
                    public void run() {
                        srl.setRefreshing(true);
                    }
                });
            }
        } else {
            // Make sure setRefreshing() is called after the layout is done with everything else.
            srl.post(new Runnable() {
                @Override
                public void run() {
                    srl.setRefreshing(false);
                }
            });
        }
    }

    @Override
    public void populateStores(final List<Store> storeList) {
        if (storeList != null && storeList.size() > 0) {
            String[] items = new String[storeList.size()];
            for (int i = 0; i < storeList.size(); i++) {
                items[i] = storeList.get(i).getName();
            }
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mStoreSpinner.setAdapter(adapter);
            mStoreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedStore = storeList.get(i);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            mStoreSpinner.setVisibility(View.GONE);
        }
    }

    @Override
    public void showTokensList() {
        //todo: Find a better way to avoid crash
        if (getActivity() == null)
            return;
//        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }
}
