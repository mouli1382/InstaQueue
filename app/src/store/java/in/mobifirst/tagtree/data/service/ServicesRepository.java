package in.mobifirst.tagtree.data.service;

import android.support.annotation.NonNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import in.mobifirst.tagtree.model.Service;
import rx.Observable;
import rx.Subscriber;

@Singleton
public class ServicesRepository implements ServicesDataSource {

    private final ServicesDataSource mServicesDataSource;

    @Inject
    ServicesRepository(ServicesDataSource servicesDataSource) {
        mServicesDataSource = servicesDataSource;
    }

    @Override
    public Observable<List<Service>> getServices() {
        return mServicesDataSource.getServices();
    }

    @Override
    public Observable<Service> getService(@NonNull String serviceId) {
        return mServicesDataSource.getService(serviceId);
    }

    @Override
    public void addNewService(@NonNull Service service, Subscriber<? super String> subscriber) {
        mServicesDataSource.addNewService(service, subscriber);
    }
}
