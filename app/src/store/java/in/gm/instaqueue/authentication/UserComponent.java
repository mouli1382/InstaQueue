package in.gm.instaqueue.authentication;

import dagger.Component;
import in.gm.instaqueue.application.ApplicationComponent;
import in.gm.instaqueue.authentication.digits.DigitsSignInActivity;
import in.gm.instaqueue.authentication.scope.UserScoped;
import in.gm.instaqueue.database.DatabaseModule;

@UserScoped
@Component(dependencies = ApplicationComponent.class, modules = { AuthenticationModule.class, DatabaseModule.class})
public interface UserComponent {

    void inject(DigitsSignInActivity digitsSignInActivity);
}
