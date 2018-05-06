package com.halfhp.rxtracer;

import org.junit.Ignore;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

@Ignore
public class ExampleService {

    public Observable<Foo> getFooObservable() {
        return Observable.<Foo>fromCallable(() -> {
            throw new RuntimeException("Bla!");
        }).subscribeOn(Schedulers.newThread());
    }

    static class Foo {}
}
