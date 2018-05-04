package com.halfhp.rxtracer;

import org.junit.BeforeClass;
import org.junit.Test;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.schedulers.Schedulers;

import static junit.framework.Assert.fail;

public class RxTracerTest {

    @BeforeClass
    public static void beforeClass() {
        RxTracer.enable();
    }

    /**
     * Not a practical test to run automatically, however for testing purposes, if the stack trace
     * contains the exact line number of the subscribe call, it means things are working.
     */
    @Test
    public void completable_runtimeException_hasSubscribeInStackTrace() throws Exception {
        final TraceChecker traceChecker = new TraceChecker();
        Completable.fromAction(() -> {
            throw new RuntimeException("Bla!");
        }).subscribeOn(Schedulers.newThread())
                .subscribe(() -> { // expect this line in the trace
                }, e -> traceChecker.check(e, 30));
        Thread.sleep(100);
        traceChecker.assertValid();
    }

    @Test
    public void observable_runtimeException_hasSubscribeInStackTrace() throws Exception {
        final TraceChecker traceChecker = new TraceChecker();
        Observable.fromCallable(() -> {
            throw new RuntimeException("Bla!");
        }).subscribeOn(Schedulers.newThread())
                .subscribe((foo) -> { // expect this line in the trace
                }, e -> traceChecker.check(e, 42));
        Thread.sleep(100);
        traceChecker.assertValid();
    }

    private static class TraceChecker {
        private String errorMessage = null;


        public void check(@NonNull Throwable e, @NonNull int lineNumber) {
            e.printStackTrace();
            final String className = RxTracerTest.class.getName();
            for(StackTraceElement element : e.getStackTrace()) {
                if(element.getClassName().equals(className) && element.getLineNumber() == lineNumber) {
                    return;
                }
            }
            this.errorMessage = "Trace missing an entry for " + className + ":" + lineNumber;
        }

        public void assertValid() {
            if(errorMessage != null) {
                fail(errorMessage);
            }
        }
    }
}