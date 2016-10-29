package in.gm.instaqueue.tokens;


import dagger.Component;
import in.gm.instaqueue.application.ApplicationComponent;
import in.gm.instaqueue.fragment.scope.FragmentScoped;

@FragmentScoped
@Component(dependencies = ApplicationComponent.class, modules = TokensPresenterModule.class)
public interface TokensComponent {

    void inject(TokensActivity activity);
}
