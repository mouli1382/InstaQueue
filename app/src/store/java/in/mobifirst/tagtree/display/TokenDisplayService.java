//package in.mobifirst.tagtree.display;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Handler;
//import android.os.Looper;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//
//import com.commonsware.cwac.preso.PresentationService;
//
//import java.util.ArrayList;
//
//import in.mobifirst.tagtree.R;
//import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
//import in.mobifirst.tagtree.tokens.Snap;
//import in.mobifirst.tagtree.tokens.SnapAdapter;
//
//public class TokenDisplayService extends PresentationService implements
//        Runnable {
//    public static final String SNAP_LIST_INTENT_KEY = "snap_list_intent_key";
//    private Handler handler = null;
//    private RecyclerView mRecyclerView;
//    private SnapAdapter mSnapAdapter;
//    private boolean flipMe = false;
//    private LinearLayoutManager mLinearLayoutManager;
//    private View mRootView;
//
//    private BroadcastReceiver mSnapBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            ArrayList<Snap> snapList = intent.getParcelableArrayListExtra(SNAP_LIST_INTENT_KEY);
//            if (snapList != null) {
//                mSnapAdapter.replaceData(snapList);
//            }
//        }
//    };
//
//    @Override
//    public void onCreate() {
//        handler = new Handler(Looper.getMainLooper());
//        mSnapAdapter = new SnapAdapter(this);
//        TTLocalBroadcastManager.registerReceiver(this, mSnapBroadcastReceiver, TTLocalBroadcastManager.TOKEN_CHANGE_INTENT_ACTION);
//        super.onCreate();
//    }
//
//    @Override
//    protected int getThemeId() {
//        return (R.style.AppTheme);
//    }
//
//    @Override
//    protected View buildPresoView(Context context, LayoutInflater inflater) {
//        mRootView = inflater.inflate(R.layout.extended_display, null);
//        mRecyclerView = (RecyclerView) mRootView.findViewById(R.id.display_recyclerview);
//        mLinearLayoutManager = new LinearLayoutManager(context);
//        mRecyclerView.setLayoutManager(mLinearLayoutManager);
//
//        run();
//
//        return (mRootView);
//    }
//
//    @Override
//    public void run() {
//        mRecyclerView.setAdapter(mSnapAdapter);
//        int itemCount = mSnapAdapter.getItemCount();
//        if (itemCount > 0) {
//            if (flipMe) {
//                //Scroll to end
//                mRecyclerView.scrollToPosition(itemCount - 1);
////                int lastVisiblePosition =
////                        mLinearLayoutManager.findLastVisibleItemPosition();
////                if (lastVisiblePosition != -1 && (lastVisiblePosition < itemCount - 1)) {
////                    mRecyclerView.scrollToPosition(itemCount - 1);
////                }
//            } else {
//                //Scroll to start
//                mRecyclerView.scrollToPosition(0);
//            }
//        }
//        flipMe = !flipMe;
//        handler.postDelayed(this, 5000);
//    }
//
//    @Override
//    public void onDestroy() {
//        handler.removeCallbacks(this);
//        TTLocalBroadcastManager.unRegisterReceiver(this, mSnapBroadcastReceiver);
//
//        super.onDestroy();
//    }
//}