package in.gm.instaqueue.tokens;


import dagger.Component;
import in.gm.instaqueue.dagger.component.ApplicationComponent;
import in.gm.instaqueue.dagger.scope.FragmentScoped;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = TokensPresenterModule.class)
public interface TokensComponent {

    void inject(TokensActivity activity);
}
