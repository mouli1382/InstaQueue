package in.gm.instaqueue.addedittoken;

import dagger.Component;
import in.gm.instaqueue.application.ApplicationComponent;
import in.gm.instaqueue.fragment.scope.FragmentScoped;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class,
        modules = AddEditTokenPresenterModule.class)
public interface AddEditTokenComponent {

    void inject(AddEditTokenActivity addEditTokenActivity);
}
