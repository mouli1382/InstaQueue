package in.mobifirst.tagtree.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.commonsware.cwac.preso.PresentationService;

import java.util.ArrayList;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.tokens.DisplayAdapter;
import in.mobifirst.tagtree.tokens.Snap;

public class TokenDisplayService extends PresentationService implements
        Runnable {
    public static final String SNAP_LIST_INTENT_KEY = "snap_list_intent_key";
    private Handler handler = null;
    private RecyclerView mRecyclerView;
    private DisplayAdapter mDisplayAdapter;
    private boolean flipMe = false;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;
    private View mRootView;
    private TextView mNoTokensTextView;

    private BroadcastReceiver mSnapBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Snap> snapList = intent.getParcelableArrayListExtra(SNAP_LIST_INTENT_KEY);
            if (snapList != null) {
                mDisplayAdapter.replaceData(snapList);
                if (snapList.size() > 0) {
                    if (mNoTokensTextView != null) {
                        mNoTokensTextView.setVisibility(View.GONE);
                    }
                } else {
                    if (mNoTokensTextView != null) {
                        mNoTokensTextView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    };


    @Override
    public void onCreate() {
        handler = new Handler(Looper.getMainLooper());
        mDisplayAdapter = new DisplayAdapter(this);
        TTLocalBroadcastManager.registerReceiver(this, mSnapBroadcastReceiver, TTLocalBroadcastManager.TOKEN_CHANGE_INTENT_ACTION);
        super.onCreate();
    }

    @Override
    protected int getThemeId() {
        return (R.style.AppTheme);
    }

    @Override
    protected View buildPresoView(Context context, LayoutInflater inflater) {
        mRootView = inflater.inflate(R.layout.extended_display, null);
        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.display_recyclerview);
        mNoTokensTextView = (TextView) mRootView.findViewById(R.id.noTokensTextView);
        mNoTokensTextView.setVisibility(View.GONE);
        mGridLayoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false);
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(context, R.dimen.item_offset);
        mRecyclerView.addItemDecoration(itemDecoration);
//        mLinearLayoutManager = new LinearLayoutManager(context);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mDisplayAdapter);

        run();

        return (mRootView);
    }

    @Override
    public void run() {
        int itemCount = mDisplayAdapter.getItemCount();
        if (itemCount > 0) {
            mNoTokensTextView.setVisibility(View.GONE);
            int firstVisiblePosition =
                    mGridLayoutManager.findFirstVisibleItemPosition();
            int lastVisiblePosition =
                    mGridLayoutManager.findLastVisibleItemPosition();
            int window = lastVisiblePosition - firstVisiblePosition;
            int scrollBy = lastVisiblePosition + window / 2;
            Log.e("TokenDisplayService", "scrollBy = " + scrollBy);
            if (scrollBy > 0 && scrollBy < itemCount) {
                mRecyclerView.scrollToPosition(scrollBy);
            } else {
                if (!flipMe) {
                    //Scroll to end to show the non-window multiples
                    mRecyclerView.scrollToPosition(itemCount - 1);
                    flipMe = !flipMe;
                } else {
                    //Scroll to start
                    mRecyclerView.scrollToPosition(0);
                    flipMe = !flipMe;
                }
            }
        } else {
            mNoTokensTextView.setVisibility(View.VISIBLE);
        }
//        if (flipMe) {
//            int lastVisiblePosition =
//                    mGridLayoutManager.findLastCompletelyVisibleItemPosition();
//            if (lastVisiblePosition != RecyclerView.NO_POSITION && lastVisiblePosition < itemCount) {
//                mRecyclerView.scrollToPosition(lastVisiblePosition);
//            }
//            if (flipMe) {
//                //Scroll to end
////                mRecyclerView.scrollToPosition(itemCount - 1);
//
//                } else {
//                    flipMe = !flipMe;
//                }
//            } else {
//                //Scroll to start
//                mRecyclerView.scrollToPosition(0);
//                flipMe = !flipMe;
//            }
//        }
        handler.postDelayed(this, 5000);
    }

    @Override
    public void onDestroy() {
        handler.removeCallbacks(this);
        TTLocalBroadcastManager.unRegisterReceiver(this, mSnapBroadcastReceiver);

        super.onDestroy();
    }
}