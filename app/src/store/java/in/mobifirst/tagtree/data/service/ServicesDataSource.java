package in.mobifirst.tagtree.data.service;

import android.support.annotation.NonNull;

import java.util.List;

import in.mobifirst.tagtree.model.Service;
import rx.Observable;
import rx.Subscriber;

public interface ServicesDataSource {

    Observable<List<Service>> getServices();

    Observable<Service> getService(@NonNull String serviceId);

    void addNewService(@NonNull Service service, Subscriber<? super String> subscriber);
}
