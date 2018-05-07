package com.halfhp.rxtracer;

import org.junit.BeforeClass;
import org.junit.Test;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static junit.framework.Assert.fail;

public class RxTracerTest {

    private ExampleService exampleService = new ExampleService();

    @BeforeClass
    public static void beforeClass() {
        RxTracer.enable();
    }

    @Test
    public void completable_runtimeException_hasSubscribeInStackTrace() throws Exception {
        final TraceChecker traceChecker = new TraceChecker();
        exampleService.getFooObservable()
                .flatMapCompletable(new Function<ExampleService.Foo, CompletableSource>() {
                    @Override
                    public CompletableSource apply(ExampleService.Foo foo) {
                        return Completable.complete();
                    }
                })
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception { // expect this line in the trace
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) throws Exception {
                        traceChecker.check(e, 38);
                    }
                });
        Thread.sleep(100);
        traceChecker.assertValid();
    }

    @Test
    public void observable_runtimeException_hasSubscribeInStackTrace() throws Exception {
        final TraceChecker traceChecker = new TraceChecker();
        exampleService.getFooObservable()
                .subscribe(new Consumer<ExampleService.Foo>() {
                    @Override
                    public void accept(ExampleService.Foo foo) { // expect this line in the trace
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) {
                        traceChecker.check(e, 56);
                    }
                });
        Thread.sleep(100);
        traceChecker.assertValid();
    }

    @Test
    public void single_runtimeException_hasSubscribeInStackTrace() throws Exception {
        final TraceChecker traceChecker = new TraceChecker();
        exampleService.getFooObservable()
                .singleOrError()
                .subscribe(new Consumer<ExampleService.Foo>() {
                    @Override
                    public void accept(ExampleService.Foo foo) { // expect this line in the trace
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) {
                        traceChecker.check(e, 75);
                    }
                });
        Thread.sleep(100);
        traceChecker.assertValid();
    }

    @Test
    public void maybe_runtimeException_hasSubscribeInStackTrace() throws Exception {
        final TraceChecker traceChecker = new TraceChecker();
        exampleService.getFooObservable()
                .singleElement()
                .subscribe(new Consumer<ExampleService.Foo>() {
                    @Override
                    public void accept(ExampleService.Foo foo) { // expect this line in the trace
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) {
                        traceChecker.check(e, 94);
                    }
                });
        Thread.sleep(100);
        traceChecker.assertValid();
    }


    private static class TraceChecker {
        private String errorMessage = null;


        void check(@NonNull Throwable e, @NonNull int lineNumber) {
            e.printStackTrace();
            final String className = RxTracerTest.class.getName();
            for (StackTraceElement element : e.getStackTrace()) {
                if (element.getClassName().equals(className) && element.getLineNumber() == lineNumber) {
                    return;
                }
            }
            this.errorMessage = "Trace missing an entry for " + className + ":" + lineNumber;
        }

        void assertValid() {
            if (errorMessage != null) {
                fail(errorMessage);
            }
        }
    }
}