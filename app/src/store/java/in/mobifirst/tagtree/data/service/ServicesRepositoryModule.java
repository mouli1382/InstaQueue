package in.mobifirst.tagtree.data.service;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;

@Module
public class ServicesRepositoryModule {

    private ServicesDataSourceImpl mServicesDataSourceImpl;

    @Singleton
    @Provides
    ServicesDataSource provideServicesDataSource(FirebaseDatabaseManager firebaseDatabaseManager, FirebaseAuthenticationManager firebaseAuthenticationManager) {
        if (null == mServicesDataSourceImpl)
            mServicesDataSourceImpl = new ServicesDataSourceImpl(firebaseDatabaseManager, firebaseAuthenticationManager);
        return mServicesDataSourceImpl;
    }
}
