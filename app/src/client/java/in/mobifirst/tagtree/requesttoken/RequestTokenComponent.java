package in.mobifirst.tagtree.requesttoken;

import dagger.Component;
import in.mobifirst.tagtree.application.ApplicationComponent;
import in.mobifirst.tagtree.fragment.scope.FragmentScoped;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class,
        modules = RequestTokenPresenterModule.class)
public interface RequestTokenComponent {

    void inject(RequestTokenActivity requestTokenActivity);
}
