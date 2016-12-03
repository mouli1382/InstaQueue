package in.mobifirst.tagtree.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.commonsware.cwac.preso.PresentationService;

import java.util.ArrayList;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.tokens.Snap;
import in.mobifirst.tagtree.tokens.SnapAdapter;

public class TokenDisplayService extends PresentationService implements
        Runnable {
    public static final String SNAP_LIST_INTENT_KEY = "snap_list_intent_key";
    private Handler handler = null;
    private RecyclerView mRecyclerView;
    private SnapAdapter mSnapAdapter;


    private BroadcastReceiver mSnapBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Snap> snapList = intent.getParcelableArrayListExtra(SNAP_LIST_INTENT_KEY);
            if (snapList != null && snapList.size() > 0) {
                mSnapAdapter.replaceData(snapList);
            }
        }
    };

    @Override
    public void onCreate() {
        handler = new Handler(Looper.getMainLooper());
        mSnapAdapter = new SnapAdapter(this);
        TTLocalBroadcastManager.registerReceiver(this, mSnapBroadcastReceiver, TTLocalBroadcastManager.TOKEN_CHANGE_INTENT_ACTION);
        super.onCreate();
    }

    @Override
    protected int getThemeId() {
        return (R.style.AppTheme);
    }

    @Override
    protected View buildPresoView(Context context, LayoutInflater inflater) {
        mRecyclerView = (RecyclerView) inflater.inflate(R.layout.content_extended_display, null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mSnapAdapter);

        run();

        return (mRecyclerView);
    }

    @Override
    public void run() {
        //Use this to scroll the list for showing the entire screen.
        handler.postDelayed(this, 1000);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(this);
        TTLocalBroadcastManager.unRegisterReceiver(this, mSnapBroadcastReceiver);

        super.onDestroy();
    }
}