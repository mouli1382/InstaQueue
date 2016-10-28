package in.gm.instaqueue.data.token;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {TasksRepositoryModule.class, ApplicationModule.class})
public interface TokensRepositoryComponent {

    TasksRepository getTasksRepository();
}
