package in.gm.instaqueue.tokens;


import dagger.Component;
import in.gm.instaqueue.fragment.scope.FragmentScoped;

@FragmentScoped
@Component(modules = TokensPresenterModule.class)
public interface TokensComponent {

    void inject(TokensActivity activity);
}
