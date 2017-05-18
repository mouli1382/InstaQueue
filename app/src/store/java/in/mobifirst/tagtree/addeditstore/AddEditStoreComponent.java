package in.mobifirst.tagtree.addeditstore;

import dagger.Component;
import in.mobifirst.tagtree.application.ApplicationComponent;
import in.mobifirst.tagtree.fragment.scope.FragmentScoped;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = AddEditStorePresenterModule.class)
public interface AddEditStoreComponent {

    void inject(AddEditStoreActivity settingsActivity);
}
