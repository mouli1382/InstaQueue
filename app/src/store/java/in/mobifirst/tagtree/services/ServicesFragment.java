package in.mobifirst.tagtree.services;

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
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.activity.RequestPermissionsActivity;
import in.mobifirst.tagtree.addeditservice.AddEditServiceActivity;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.fragment.BaseFragment;
import in.mobifirst.tagtree.model.Service;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.util.NetworkConnectionUtils;
import in.mobifirst.tagtree.view.ScrollChildSwipeRefreshLayout;

public class ServicesFragment extends BaseFragment implements ServicesContract.View {

    @Inject
    IQSharedPreferences mIQSharedPreferences;

    @Inject
    protected NetworkConnectionUtils mNetworkConnectionUtils;

    private ServicesContract.Presenter mPresenter;

    private ServicesAdapter mServicesAdapter;

    private View mNoServicesView;

    private ImageView mNoServicesIcon;

    private TextView mNoServicesMainView;

    private TextView mNoServicesAddView;

    private LinearLayout mServicesView;

    public ServicesFragment() {
        // Requires empty public constructor
    }

    public static ServicesFragment newInstance() {
        return new ServicesFragment();
    }


    private BroadcastReceiver mNetworkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra(TTLocalBroadcastManager.NETWORK_STATUS_KEY, false);

            if (!isConnected && getView() != null) {
                setLoadingIndicator(false);
                showNetworkError(getView());
            } else {
                if (getView() != null) {
                    mPresenter.loadServices();
                }
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((IQStoreApplication) getActivity().getApplicationContext()).getApplicationComponent()
                .inject(this);
        mServicesAdapter = new ServicesAdapter(getActivity(), new ArrayList<Service>(0), mItemListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        //ToDo for now just check for the connectivity and show it in a snackbar.
        // Need to give user capability to refresh when SwipeToRefresh along with Rx and MVP is brought in.
        if (!mNetworkConnectionUtils.isConnected()) {
            setLoadingIndicator(false);
            showNetworkError(getView());
        } else {
            mPresenter.subscribe();
        }
        TTLocalBroadcastManager.registerReceiver(getActivity(), mNetworkBroadcastReceiver, TTLocalBroadcastManager.NETWORK_INTENT_ACTION);
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
        TTLocalBroadcastManager.unRegisterReceiver(getActivity(), mNetworkBroadcastReceiver);
    }

    @Override
    public void setPresenter(@NonNull ServicesContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode, data);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set up floating action button
        FloatingActionButton fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewService();
            }
        });
        fab.setVisibility(View.VISIBLE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_services, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(mServicesAdapter);

        mServicesView = (LinearLayout) root.findViewById(R.id.servicesLL);

        // Set up  no Tokens view
        mNoServicesView = root.findViewById(R.id.noServices);
        mNoServicesIcon = (ImageView) root.findViewById(R.id.noServicesIcon);
        mNoServicesMainView = (TextView) root.findViewById(R.id.noServicesMain);
        mNoServicesAddView = (TextView) root.findViewById(R.id.noServicesAdd);
        mNoServicesAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddService();
            }
        });

//        setRetainInstance(true);

        // Set up progress indicator
        final ScrollChildSwipeRefreshLayout swipeRefreshLayout =
                (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );
        // Set the scrolling view in the custom SwipeRefreshLayout.
        swipeRefreshLayout.setScrollUpChild(recyclerView);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mNetworkConnectionUtils.isConnected()) {
                    mPresenter.loadServices();
                } else {
                    setLoadingIndicator(false);
                }
            }
        });

        return root;
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
    public void showServices(List<Service> services) {
        if (isActive()) {
            mServicesAdapter.replaceData(services);

            mServicesView.setVisibility(View.VISIBLE);
            mNoServicesView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showAddService() {
        Intent intent = new Intent(getContext(), AddEditServiceActivity.class);
        startActivityForResult(intent, AddEditServiceActivity.REQUEST_ADD_SERVICE);
    }

    @Override
    public void editService(String storeUid, String serviceUid) {
        Intent intent = new Intent(getContext(), AddEditServiceActivity.class);
        intent.putExtra(ApplicationConstants.STORE_UID, storeUid);
        intent.putExtra(ApplicationConstants.SERVICE_UID, serviceUid);
        startActivityForResult(intent, AddEditServiceActivity.REQUEST_ADD_SERVICE);
    }

    @Override
    public void showLoadingServicesError() {
        showMessage(getString(R.string.loading_services_error));
    }

    @Override
    public void showNoServices() {
        showNoServicesViews(
                getResources().getString(R.string.no_services_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false
        );
    }

    @Override
    public void showServiceSavedMessage() {
        showMessage(getString(R.string.successfully_saved_service_message));
    }

    private void showNoServicesViews(String mainText, int iconRes, boolean showAddView) {
        mServicesView.setVisibility(View.GONE);
        mNoServicesView.setVisibility(View.VISIBLE);

        mNoServicesMainView.setText(mainText);
        mNoServicesIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoServicesAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }


    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void showTokensList(Service service) {
        RequestPermissionsActivity.start(getActivity(), service);
    }

    /**
     * Listener for clicks on Service in the RecyclerView.
     */
    ServicesFragment.ServiceItemListener mItemListener = new ServicesFragment.ServiceItemListener() {
        @Override
        public void onServiceClick(Service service) {
            showTokensList(service);
        }

        @Override
        public void onServiceLongClick(Service service) {
            mPresenter.openServiceDetails(service);
        }
    };

    public interface ServiceItemListener {
        void onServiceClick(Service service);

        void onServiceLongClick(Service service);
    }
}
