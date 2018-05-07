package com.halfhp.rxtracer;

import org.junit.Ignore;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.Callable;

@Ignore
public class ExampleService {

    public Observable<Foo> getFooObservable() {
        return Observable.fromCallable(new Callable<Foo>() {
            @Override
            public Foo call() throws Exception {
                throw new RuntimeException("Bla!");
            }
        }).subscribeOn(Schedulers.newThread());
    }

    static class Foo {}
}
