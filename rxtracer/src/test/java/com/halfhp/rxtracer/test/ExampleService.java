package com.halfhp.rxtracer.test;

import org.junit.Ignore;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.Callable;

import static org.junit.Assert.fail;

@Ignore
public class ExampleService {

    public Observable<Foo> getFooObservable() {
        return Observable.fromCallable(new Callable<Foo>() {
            @Override
            public Foo call() {
                throw new RuntimeException("Bla!");
            }
        }).subscribeOn(Schedulers.newThread());
    }

    public Observable<Bar> getBarObservable() {
        return Observable.fromCallable(new Callable<Bar>() {
            @Override
            public Bar call() {
                return new Bar();
            }
        }).subscribeOn(Schedulers.newThread());
    }

    public static class Foo {}
    public static class Bar {}

    public static class FailOnSuccessConsumer implements Consumer<Foo> {

        @Override
        public void accept(Foo foo) throws Exception {
            fail("Exception expected");
        }
    }

    public static class FailOnSuccessAction implements Action {

        @Override
        public void run() throws Exception {
            fail("Exception expected");
        }
    }

    public static class CheckTraceOnErrorConsumer implements Consumer<Throwable> {

        @NonNull
        private TraceChecker traceChecker;

        public CheckTraceOnErrorConsumer(@NonNull TraceChecker traceChecker) {
            this.traceChecker = traceChecker;
        }

        @Override
        public void accept(Throwable throwable) throws Exception {
            traceChecker.check(throwable);
        }
    }
}
