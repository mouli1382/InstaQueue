package in.mobifirst.tagtree.addedittoken;

import dagger.Component;
import in.mobifirst.tagtree.application.ApplicationComponent;
import in.mobifirst.tagtree.fragment.scope.FragmentScoped;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class,
        modules = AddEditTokenPresenterModule.class)
public interface AddEditTokenComponent {

    void inject(AddEditTokenActivity addEditTokenActivity);
}
