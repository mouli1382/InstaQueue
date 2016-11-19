package in.mobifirst.tagtree.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

public class TTLocalBroadcastManager {
    public static final String NETWORK_INTENT_ACTION = "TAGTREE_NETWORK_EVENT";
    public static final String NETWORK_STATUS_KEY = "network_status_key";

    public static void registerReceiver(Context context, BroadcastReceiver receiver, String intentAction) {
        LocalBroadcastManager.getInstance(context).registerReceiver(receiver, new IntentFilter(intentAction));
    }

    public static void unRegisterReceiver(Context context, BroadcastReceiver receiver) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
    }

    public static void sendBroadcast(Context context, Intent intent) {
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static void sendNetworkStatusBroadcast(Context context, boolean isConnected) {
        Intent intent = new Intent(NETWORK_INTENT_ACTION);
        intent.putExtra(NETWORK_STATUS_KEY, isConnected);
        sendBroadcast(context, intent);
    }

}
