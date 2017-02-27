package in.mobifirst.tagtree.ftu;

import dagger.Component;
import in.mobifirst.tagtree.application.ApplicationComponent;
import in.mobifirst.tagtree.fragment.scope.FragmentScoped;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = SettingsPresenterModule.class)
public interface SettingsComponent {

    void inject(SettingsActivity settingsActivity);
}
