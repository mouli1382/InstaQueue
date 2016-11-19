package in.mobifirst.tagtree.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStateReceiver extends BroadcastReceiver {
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        NetworkInfo currentNetworkInfo = connectivityManager.getActiveNetworkInfo();

        boolean connectionStatus = currentNetworkInfo != null && currentNetworkInfo.isConnected();

        TTLocalBroadcastManager.sendNetworkStatusBroadcast(context, connectionStatus);
    }
}