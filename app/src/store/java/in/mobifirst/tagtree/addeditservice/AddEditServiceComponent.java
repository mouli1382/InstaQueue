package in.mobifirst.tagtree.addeditservice;

import dagger.Component;
import in.mobifirst.tagtree.application.ApplicationComponent;
import in.mobifirst.tagtree.fragment.scope.FragmentScoped;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = AddEditServicePresenterModule.class)
public interface AddEditServiceComponent {

    void inject(AddEditServiceActivity settingsActivity);
}
