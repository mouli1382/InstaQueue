package in.gm.instaqueue.tokens;


import dagger.Component;
import in.gm.instaqueue.dagger.scope.FragmentScoped;

@FragmentScoped
@Component(dependencies = TasksRepositoryComponent.class, modules = TokensPresenterModule.class)
public interface TokensComponent {
	
    void inject(TokensActivity activity);
}
