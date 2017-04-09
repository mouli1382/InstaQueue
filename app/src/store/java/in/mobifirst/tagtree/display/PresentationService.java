/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package in.mobifirst.tagtree.display;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.cast.CastPresentation;
import com.google.android.gms.cast.CastRemoteDisplayLocalService;

import java.util.ArrayList;
import java.util.List;

import in.mobifirst.tagtree.R;
import in.mobifirst.tagtree.application.IQStoreApplication;
import in.mobifirst.tagtree.data.token.TokensRepository;
import in.mobifirst.tagtree.receiver.TTLocalBroadcastManager;
import in.mobifirst.tagtree.tokens.DisplayAdapter;
import in.mobifirst.tagtree.tokens.Snap;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * Service to keep the remote display running even when the app goes into the background
 */
public class PresentationService extends CastRemoteDisplayLocalService implements Runnable {

    private static final String TAG = "PresentationService";

    public static final String SNAP_LIST_INTENT_KEY = "snap_list_intent_key";
    private Handler mHandler = null;
    private RecyclerView mRecyclerView;
    private DisplayAdapter mDisplayAdapter;
    private boolean flipMe = false;
    private LinearLayoutManager mLinearLayoutManager;
    private TextView mNoTokensTextView;

    private TokensRepository mTokensRepository;
    private CompositeSubscription mSubscriptions;

    // Second screen
    private CastPresentation mPresentation;

    private BroadcastReceiver mSnapBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<Snap> snaps = intent.getParcelableArrayListExtra(SNAP_LIST_INTENT_KEY);
            if (snaps != null) {
                if (mDisplayAdapter != null) {
                    mDisplayAdapter.replaceData(snaps);
                }
            }
            if (snaps.size() > 0) {
                if (mNoTokensTextView != null) {
                    mNoTokensTextView.setVisibility(View.GONE);
                }
            } else {
                if (mNoTokensTextView != null) {
                    mNoTokensTextView.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();

        mHandler = new Handler(Looper.getMainLooper());
        TTLocalBroadcastManager.registerReceiver(this, mSnapBroadcastReceiver, TTLocalBroadcastManager.TOKEN_CHANGE_INTENT_ACTION);
        TTSHelper.getInstance().init(getApplicationContext());

        mSubscriptions = new CompositeSubscription();
        mTokensRepository = ((IQStoreApplication) getApplication()).getApplicationComponent().getTokensRepository();
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(this);
        TTSHelper.getInstance().destroy();
        if (mSubscriptions != null) {
            mSubscriptions.clear();
        }
        TTLocalBroadcastManager.unRegisterReceiver(this, mSnapBroadcastReceiver);
        super.onDestroy();
    }

    @Override
    public void onCreatePresentation(Display display) {
        createPresentation(display);
    }

    @Override
    public void onDismissPresentation() {
        dismissPresentation();
    }

    private void dismissPresentation() {
        if (mPresentation != null) {
            mPresentation.dismiss();
            mPresentation = null;
        }
    }

    private void createPresentation(Display display) {
        dismissPresentation();
        mPresentation = new SecondaryScreen(this, display, R.style.AppTheme);

        try {
            mPresentation.show();
            Log.e(TAG, "Presentation Created");
            loadMeetingsForSecondaryScreen();
        } catch (WindowManager.InvalidDisplayException ex) {
            Log.e(TAG, "Unable to show presentation, display was removed.", ex);
            if (mSubscriptions != null) {
                mSubscriptions.clear();
            }
            dismissPresentation();
        }
    }

    private void loadMeetingsForSecondaryScreen() {
        mSubscriptions.clear();
        Subscription subscription = mTokensRepository
                .getSnaps()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Snap>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(List<Snap> snaps) {
                        processSnaps(snaps);
                    }
                });
        Log.e(TAG, subscription.toString());
        mSubscriptions.add(subscription);
    }

    private void processSnaps(List<Snap> snaps) {
        Log.e(TAG, "processSnaps");
        if (snaps == null || snaps.size() == 0) {
            Log.e(TAG, "broadcast being sent to display service.");
            Intent intent = new Intent(TTLocalBroadcastManager.TOKEN_CHANGE_INTENT_ACTION);
            intent.putParcelableArrayListExtra(PresentationService.SNAP_LIST_INTENT_KEY,
                    new ArrayList<Snap>());
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        } else {
            Log.e(TAG, "broadcast being sent to display service with tokens");
            Intent intent = new Intent(TTLocalBroadcastManager.TOKEN_CHANGE_INTENT_ACTION);
            intent.putParcelableArrayListExtra(PresentationService.SNAP_LIST_INTENT_KEY,
                    (ArrayList<? extends Parcelable>) snaps);
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    private class SecondaryScreen extends CastPresentation {

        public SecondaryScreen(Context context, Display display, int appTheme) {
            super(context, display, appTheme);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            setContentView(R.layout.extended_display);
            mRecyclerView = (RecyclerView) findViewById(R.id.display_recyclerview);
            mNoTokensTextView = (TextView) findViewById(R.id.noTokensTextView);
            mNoTokensTextView.setVisibility(View.GONE);
            mLinearLayoutManager = new GridLayoutManager(getContext(), 2, LinearLayoutManager.VERTICAL, false);
            ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getContext(), R.dimen.item_offset);
            mRecyclerView.addItemDecoration(itemDecoration);
//        mLinearLayoutManager = new LinearLayoutManager(context);

            mDisplayAdapter = new DisplayAdapter(getContext());

            mRecyclerView.setLayoutManager(mLinearLayoutManager);
            mRecyclerView.setAdapter(mDisplayAdapter);

            run();
        }
    }

    @Override
    public void run() {
        int itemCount = mDisplayAdapter.getItemCount();
        if (itemCount > 0) {
            mNoTokensTextView.setVisibility(View.GONE);
            int firstVisiblePosition =
                    mLinearLayoutManager.findFirstVisibleItemPosition();
            int lastVisiblePosition =
                    mLinearLayoutManager.findLastVisibleItemPosition();
            int window = lastVisiblePosition - firstVisiblePosition;
            int scrollBy = lastVisiblePosition + window / 2;
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
        mHandler.postDelayed(this, 5000);
    }
}
