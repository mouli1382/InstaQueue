package in.gm.instaqueue.app;

import android.app.Application;

import in.gm.instaqueue.dagger.component.AppComponent;
import in.gm.instaqueue.dagger.component.DaggerAppComponent;
import in.gm.instaqueue.dagger.module.AppModule;
import in.gm.instaqueue.dagger.module.DbModule;
import in.gm.instaqueue.dagger.module.PreferenceModule;

public class IQApplication extends Application {
    private AppComponent appComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        setupObjectGraph();
    }

    private void setupObjectGraph() {
        if (appComponent == null) {
            appComponent = DaggerAppComponent
                    .builder()
                    .appModule(new AppModule(this))
                    .dbModule(new DbModule())
                    .preferenceModule(new PreferenceModule())
                    .build();
        }
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }
}
