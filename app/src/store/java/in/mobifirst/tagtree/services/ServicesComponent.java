package in.mobifirst.tagtree.services;


import dagger.Component;
import in.mobifirst.tagtree.application.ApplicationComponent;
import in.mobifirst.tagtree.fragment.scope.FragmentScoped;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = ServicesPresenterModule.class)
public interface ServicesComponent {

    void inject(ServicesActivity activity);
}
