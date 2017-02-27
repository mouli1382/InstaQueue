package in.mobifirst.tagtree.tokens;


import dagger.Component;
import in.mobifirst.tagtree.application.ApplicationComponent;
import in.mobifirst.tagtree.fragment.scope.FragmentScoped;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = TokensPresenterModule.class)
public interface TokensComponent {

    void inject(TokensActivity activity);
}
