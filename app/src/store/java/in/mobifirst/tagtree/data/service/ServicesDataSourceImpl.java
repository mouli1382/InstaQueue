package in.mobifirst.tagtree.data.service;

import android.support.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import in.mobifirst.tagtree.authentication.FirebaseAuthenticationManager;
import in.mobifirst.tagtree.database.FirebaseDatabaseManager;
import in.mobifirst.tagtree.model.Service;
import rx.Observable;
import rx.Subscriber;

public class ServicesDataSourceImpl implements ServicesDataSource {

    private FirebaseDatabaseManager mFirebaseDatabaseManager;
    private FirebaseAuth mFirebaseAuth;

    public ServicesDataSourceImpl(FirebaseDatabaseManager firebaseDatabaseManager, FirebaseAuthenticationManager firebaseAuthenticationManager) {
        mFirebaseDatabaseManager = firebaseDatabaseManager;
        mFirebaseAuth = firebaseAuthenticationManager.getAuthInstance();
    }

    @Override
    public Observable<List<Service>> getServices() {
        return mFirebaseDatabaseManager.getAllServices(mFirebaseAuth.getCurrentUser().getUid());
    }

    @Override
    public Observable<Service> getService(@NonNull String serviceId) {
        return mFirebaseDatabaseManager.getServiceById(serviceId);
    }

    @Override
    public void addNewService(@NonNull Service service, Subscriber<? super String> subscriber) {
        service.setStoreId(mFirebaseAuth.getCurrentUser().getUid());
        mFirebaseDatabaseManager.addNewService(service, subscriber);
    }
}
