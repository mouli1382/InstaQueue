package in.gm.instaqueue.application;

import android.app.Application;

public class IQApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //ToDo Init configs common to both Store/ Client
    }
}
