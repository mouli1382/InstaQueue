package in.mobifirst.tagtree.addeditservice;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.florescu.android.rangeseekbar.RangeSeekBar;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.fragment.BaseFragment;
import in.mobifirst.tagtree.model.Service;
import in.mobifirst.tagtree.model.Slot;
import in.mobifirst.tagtree.preferences.IQSharedPreferences;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.util.ApplicationConstants;
import in.mobifirst.tagtree.util.NetworkConnectionUtils;


public class AddEditServiceFragment extends BaseFragment implements AddEditServiceContract.View {

    private AddEditServiceContract.Presenter mPresenter;

    private LinearLayout mDaysGroup;

    //Monday = 0
    private ToggleButton[] mDays = new ToggleButton[7];

    private FloatingActionButton fab;
    private Spinner mDaysSpinner;
    private CustomAdapter mAdapter;
    private RangeSeekBar earlymorning;
    private RangeSeekBar morning;
    private RangeSeekBar afternoon;
    private RangeSeekBar evening;
    private LinearLayout mSeekbarLayout;
    private Button mUpdateButton;
    private Switch mSwitch;

    private TextInputEditText mServiceNameEditText;
    private TextInputEditText mServiceDescEditText;
    private TextInputEditText mDurationEditText;
    private TextInputLayout mServiceDurationTextInputLayout;
    private TextInputLayout mServiceNameTextInputLayout;
    private TextInputLayout mServiceDescTextInputLayout;

    private int daysOfOperation = -1;
    private String storeUid;
    private String serviceUid;
    private String[] timeSlots;

    @Inject
    IQSharedPreferences mIQSharedPreferences;

    @Inject
    protected FirebaseAuthenticationManager mFirebaseAuth;

    @Inject
    protected NetworkConnectionUtils mNetworkConnectionUtils;

    public static AddEditServiceFragment newInstance() {
        return new AddEditServiceFragment();
    }

    private BroadcastReceiver mNetworkBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isConnected = intent.getBooleanExtra(TTLocalBroadcastManager.NETWORK_STATUS_KEY, false);

            if (!isConnected && getView() != null) {
                showNetworkError(getView());
            } else {
                mPresenter.subscribe();
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
    }

    @Override
    public void onPause() {
        super.onPause();
        TTLocalBroadcastManager.unRegisterReceiver(getActivity(), mNetworkBroadcastReceiver);
    }

    @Override
    public void setPresenter(@NonNull AddEditServiceContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((IQStoreApplication) getActivity().getApplicationContext()).getApplicationComponent()
                .inject(this);

        mPresenter.subscribe();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.unsubscribe();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fab =
                (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mNetworkConnectionUtils.isConnected()) {
                    if (validateInput()) {
                        Service service = new Service(storeUid
                                , mServiceNameEditText.getText().toString()
                                , mServiceDescEditText.getText().toString()
                                , daysOfOperation
                                , Integer.parseInt(mDurationEditText.getText().toString()));
                        service.setSlots(mAdapter.getmItems());
                        if (!TextUtils.isEmpty(serviceUid)) {
                            service.setId(serviceUid);
                            mPresenter.editServiceDetails(service);
                        } else {
                            mPresenter.addServiceDetails(service);
                        }
                    }
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_addedit_service, container, false);

        Bundle bundle = getArguments();
        storeUid = bundle.getString(ApplicationConstants.STORE_UID);

        mServiceNameTextInputLayout = (TextInputLayout) root.findViewById(R.id.serviceNameInputLayout);
        mServiceDescTextInputLayout = (TextInputLayout) root.findViewById(R.id.serviceDescInputLayout);
        mServiceDurationTextInputLayout = (TextInputLayout) root.findViewById(R.id.serviceDurationInputLayout);

        mServiceNameEditText = (TextInputEditText) root.findViewById(R.id.serviceName);
        mServiceNameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mServiceNameTextInputLayout.setError(getString(R.string.empty_store_name));
                    mServiceNameTextInputLayout.setErrorEnabled(true);
                } else {
                    mServiceNameTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        mServiceDescEditText = (TextInputEditText) root.findViewById(R.id.description);
        mServiceDescEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mServiceDescTextInputLayout.setError(getString(R.string.empty_store_area));
                    mServiceDescTextInputLayout.setErrorEnabled(true);
                } else {
                    mServiceDescTextInputLayout.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mDurationEditText = (TextInputEditText) root.findViewById(R.id.duration);
        mDurationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(charSequence)) {
                    mServiceDurationTextInputLayout.setError(getString(R.string.empty_store_counters));
                    mServiceDurationTextInputLayout.setErrorEnabled(true);
                } else {
                    int counterValue = Integer.parseInt(charSequence.toString());
                    if (counterValue > 0 && counterValue < 60) {
                        mServiceDurationTextInputLayout.setErrorEnabled(false);
                    } else {
                        mServiceDurationTextInputLayout.setError(getString(R.string.invalid_duration));
                        mServiceDurationTextInputLayout.setErrorEnabled(true);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        timeSlots = new String[4];
        mDaysGroup = (LinearLayout) root.findViewById(R.id.weekGroup);
        mDaysSpinner = (Spinner) root.findViewById(R.id.spinner);
        earlymorning = (RangeSeekBar) root.findViewById(R.id.earlymorning);
        earlymorning.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Number minValue, Number maxValue) {
                timeSlots[0] = earlymorning.getSelectedMinValue() + ":" + earlymorning.getSelectedMaxValue();
            }
        });
        morning = (RangeSeekBar) root.findViewById(R.id.morning);
        morning.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Number minValue, Number maxValue) {
                timeSlots[1] = morning.getSelectedMinValue() + ":" + morning.getSelectedMaxValue();
            }
        });
        afternoon = (RangeSeekBar) root.findViewById(R.id.afternoon);
        afternoon.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Number minValue, Number maxValue) {
                timeSlots[2] = afternoon.getSelectedMinValue() + ":" + afternoon.getSelectedMaxValue();
            }
        });
        evening = (RangeSeekBar) root.findViewById(R.id.evening);
        evening.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Number minValue, Number maxValue) {
                timeSlots[3] = evening.getSelectedMinValue() + ":" + evening.getSelectedMaxValue();
            }
        });
        mSeekbarLayout = (LinearLayout) root.findViewById(R.id.seekbar_placeholder);
        mSwitch = (Switch) root.findViewById(R.id.switch1);

        for (int i = 0; i < 7; ++i) {
            mDays[i] = (ToggleButton) mDaysGroup.getChildAt(i);
        }

        if (-1 != daysOfOperation) {
            for (int i = 0; i < 7; ++i) {
                mDays[i].setChecked((daysOfOperation & (1 << i)) != 0);
            }
        }

        Button submit = (Button) root.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int daysMask = 0; //0111 1111 -> days mask.
                for (int i = 0; i < 7; ++i) {
                    if (mDays[i].isChecked()) {
                        daysMask |= 1 << i;
                    }
                }
                daysOfOperation = daysMask;
                loadSpinner(daysMask);
            }
        });

        mUpdateButton = (Button) root.findViewById(R.id.update);
        mUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(
                        (TextUtils.isEmpty(timeSlots[0]) ? "" : timeSlots[0]) + ";"
                                + (TextUtils.isEmpty(timeSlots[1]) ? "" : timeSlots[1]) + ";"
                                + (TextUtils.isEmpty(timeSlots[2]) ? "" : timeSlots[2]) + ";"
                                + (TextUtils.isEmpty(timeSlots[3]) ? "" : timeSlots[3])
                );

                if (mSwitch.isChecked()) {
                    List<Slot> items = mAdapter.getmItems();
                    for (Slot slot : items) {
                        slot.setTimeSlots(stringBuilder.toString());
                    }
                } else {
                    Slot slot = mAdapter.getItem(mDaysSpinner.getSelectedItemPosition());
                    slot.setTimeSlots(stringBuilder.toString());
                }
            }
        });

        if (-1 != daysOfOperation) {
            loadSpinner(daysOfOperation);
        }

        return root;
    }

    private void loadSpinner(int daysOfOperation) {
        if (daysOfOperation > 0) {
            mDaysSpinner.setVisibility(View.VISIBLE);
            List<Slot> items = new ArrayList<>();
            for (int i = 0; i < 7; ++i) {
                if (mDays[i].isChecked()) {
                    items.add(new Slot(mDays[i].getText().toString(), 1 << i, ""));
                }
            }

            mAdapter = new CustomAdapter(getActivity(), items);
            mDaysSpinner.setAdapter(mAdapter);
            mDaysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    addTimeRangeSelectors(mAdapter.getItem(i));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            mDaysSpinner.setVisibility(View.GONE);
            mSeekbarLayout.setVisibility(View.GONE);
            mUpdateButton.setVisibility(View.GONE);
            mSwitch.setVisibility(View.GONE);
        }
    }

    private void loadSpinner(int daysOfOperation, List<Slot> slots) {
        if (daysOfOperation > 0) {
            mDaysSpinner.setVisibility(View.VISIBLE);

            mAdapter = new CustomAdapter(getActivity(), slots);
            mDaysSpinner.setAdapter(mAdapter);
            mDaysSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    addTimeRangeSelectors(mAdapter.getItem(i));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            mDaysSpinner.setVisibility(View.GONE);
            mSeekbarLayout.setVisibility(View.GONE);
            mUpdateButton.setVisibility(View.GONE);
            mSwitch.setVisibility(View.GONE);
        }
    }

    private void addTimeRangeSelectors(Slot slot) {
        mSeekbarLayout.setVisibility(View.VISIBLE);
        String dayHours = slot.getTimeSlots();
        if (TextUtils.isEmpty(dayHours)) {
            timeSlots = new String[4];
            earlymorning.resetSelectedValues();
            morning.resetSelectedValues();
            afternoon.resetSelectedValues();
            evening.resetSelectedValues();
        } else {
            String[] tokens = dayHours.split(";");
            if (tokens != null && tokens.length > 0) {
                String[] values;
                double min;
                double max;
                if (!TextUtils.isEmpty(tokens[0])) {
                    values = tokens[0].split(":");
                    min = Double.valueOf(values[0]);
                    max = Double.valueOf(values[1]);
                    earlymorning.setSelectedMinValue(min);
                    earlymorning.setSelectedMaxValue(max);
                    timeSlots[0] = min + ":" + max;
                } else {
                    timeSlots[0] = "";
                    earlymorning.resetSelectedValues();
                }

                if (!TextUtils.isEmpty(tokens[1])) {
                    values = tokens[1].split(":");
                    min = Double.valueOf(values[0]);
                    max = Double.valueOf(values[1]);
                    morning.setSelectedMinValue(min);
                    morning.setSelectedMaxValue(max);
                    timeSlots[1] = min + ":" + max;
                } else {
                    timeSlots[1] = "";
                    morning.resetSelectedValues();
                }

                if (!TextUtils.isEmpty(tokens[2])) {
                    values = tokens[2].split(":");
                    min = Double.valueOf(values[0]);
                    max = Double.valueOf(values[1]);
                    afternoon.setSelectedMinValue(Double.valueOf(values[0]));
                    afternoon.setSelectedMaxValue(Double.valueOf(values[1]));
                    timeSlots[2] = min + ":" + max;
                } else {
                    timeSlots[2] = "";
                    afternoon.resetSelectedValues();
                }

                if (!TextUtils.isEmpty(tokens[3])) {
                    values = tokens[3].split(":");
                    min = Double.valueOf(values[0]);
                    max = Double.valueOf(values[1]);
                    evening.setSelectedMinValue(Double.valueOf(values[0]));
                    evening.setSelectedMaxValue(Double.valueOf(values[1]));
                    timeSlots[3] = min + ":" + max;
                } else {
                    timeSlots[3] = "";
                    evening.resetSelectedValues();
                }
            }
        }
        mSwitch.setVisibility(View.VISIBLE);
        mUpdateButton.setVisibility(View.VISIBLE);
    }

    private static class CustomAdapter extends BaseAdapter {
        private List<Slot> mItems;
        private Context mContext;

        public CustomAdapter(Context context, List<Slot> items) {
            mItems = items;
            mContext = context;
        }

        public List<Slot> getmItems() {
            return mItems;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Slot getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(android.R.layout.simple_spinner_dropdown_item, null);

            }
            ((TextView) convertView).setText(mItems.get(position).getDay());
            return convertView;
        }
    }

    @Override
    public void showEmptyServiceError() {
        Snackbar.make(getView(), getString(R.string.empty_service_details), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showAddServiceFailedError() {
        Snackbar.make(getView(), getString(R.string.add_service_failed), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showEditServiceFailedError() {
        Snackbar.make(getView(), getString(R.string.edit_service_failed), Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showServicesList(Service service) {
        if (getActivity() == null)
            return;

        mIQSharedPreferences.putBoolean(ApplicationConstants.FTU_COMPLETED_KEY, true);
        mIQSharedPreferences.putString(ApplicationConstants.STORE_UID, mFirebaseAuth.getAuthInstance().getCurrentUser().getUid());

        getActivity().setResult(Activity.RESULT_OK);
        getActivity().finish();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void populateService(Service service) {
        if (isAdded() && service != null) {
            serviceUid = service.getId();

            mServiceNameEditText.setText(service.getName());
            mServiceDescEditText.setText(service.getDescription());
            mDurationEditText.setText(service.getDuration() + "");

            daysOfOperation = service.getDaysOfOperation();
            if (-1 != daysOfOperation) {
                for (int i = 0; i < 7; ++i) {
                    mDays[i].setChecked((daysOfOperation & (1 << i)) != 0);
                }
            }

            loadSpinner(daysOfOperation, service.getSlots());
        }
    }


    private boolean validateInput() {
        boolean result = false;

        CharSequence storeName = mServiceNameEditText.getText();
        if (TextUtils.isEmpty(storeName)) {
            mServiceNameTextInputLayout.setError(getString(R.string.empty_service_name));
            mServiceNameTextInputLayout.setErrorEnabled(true);
            return result;
        }

        CharSequence storeArea = mServiceDescEditText.getText();
        if (TextUtils.isEmpty(storeArea)) {
            mServiceDescTextInputLayout.setError(getString(R.string.empty_service_desc));
            mServiceDescTextInputLayout.setErrorEnabled(true);
            return result;
        }

        CharSequence counters = mDurationEditText.getText();
        if (TextUtils.isEmpty(counters)) {
            mServiceDurationTextInputLayout.setError(getString(R.string.empty_store_counters));
            mServiceDurationTextInputLayout.setErrorEnabled(true);
            return result;
        } else {
            int counterValue = Integer.parseInt(counters.toString());
            if (counterValue < 1 || counterValue > 60) {
                mServiceDurationTextInputLayout.setError(getString(R.string.invalid_duration));
                mServiceDurationTextInputLayout.setErrorEnabled(true);
                return result;
            }
        }

        if (daysOfOperation == -1) {
            showMessage(getView(), "Select working Days.");
            return result;
        }

        List<Slot> slots = mAdapter.getmItems();
        if (slots == null || slots.size() == 0) {
            showMessage(getView(), "Select working Hours.");
            return result;
        }

        mServiceNameTextInputLayout.setErrorEnabled(false);
        mServiceDescTextInputLayout.setErrorEnabled(false);
        mServiceDurationTextInputLayout.setErrorEnabled(false);

        return true;
    }
}
