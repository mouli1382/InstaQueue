package in.mobifirst.tagtree.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkConnectionUtils {

    private final String WIFI = "WIFI";
    private final String MOBILE = "MOBILE";

    private Context mContext;
    private ConnectivityManager mConnectionManager;

    public NetworkConnectionUtils(Context context) {
        mContext = context;
    }

    public boolean isConnected() {
        mConnectionManager = mConnectionManager != null ? mConnectionManager : (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = mConnectionManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public boolean isWifiConnected() {
        return isConnected() && mConnectionManager.getActiveNetworkInfo().getTypeName().equals(WIFI);
    }

    public boolean isCellularConnected() {
        return isConnected() && mConnectionManager.getActiveNetworkInfo().getTypeName().equals(MOBILE);
    }
}