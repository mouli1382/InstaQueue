package in.gm.instaqueue.dagger.component;

import javax.inject.Singleton;

import dagger.Component;
import in.gm.instaqueue.activity.BaseActivity;
import in.gm.instaqueue.dagger.module.AppModule;
import in.gm.instaqueue.dagger.module.DbModule;
import in.gm.instaqueue.dagger.module.PreferenceModule;
import in.gm.instaqueue.fragment.BaseFragment;

@Singleton
@Component(modules = {AppModule.class, DbModule.class, PreferenceModule.class})
public interface AppComponent {
    //ToDo move the bindings to activity scope once we separate activity specific dependencies.
    void inject(BaseActivity baseActivity);

    void inject(BaseFragment baseFragment);
}