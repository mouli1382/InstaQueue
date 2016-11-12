package in.mobifirst.tagtree.application;

import android.support.multidex.MultiDexApplication;

public class IQApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        //ToDo Init configs common to both Store/ Client
    }
}
