package com.halfhp.rxtracer;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;

public class RxTracerTest {

    private ExampleService exampleService = new ExampleService();

    @BeforeClass
    public static void beforeClass() {
        RxTracer.enable();
    }

    @Before
    public void before() {
        RxTracer.setMode(RxTracer.Mode.REWRITE);
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
                    public void run() { // expect this line in the trace
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable e) {
                        traceChecker.check(e, 41);
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
                        traceChecker.check(e, 59);
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
                        traceChecker.check(e, 78);
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
                        traceChecker.check(e, 97);
                    }
                });
        Thread.sleep(100);
        traceChecker.assertValid();
    }

    @Test
    public void rewriteStacktrace_append_addsNewTraceToEnd() {
        RxTracer.setMode(RxTracer.Mode.APPEND);
        final StackTraceElement[] newTrace = new Exception().getStackTrace();
        final Exception ex1 = new Exception();
        final StackTraceElement[] rawTrace = ex1.getStackTrace();

        assertEquals(rawTrace.length + newTrace.length, RxTracer.rewriteStackTrace(ex1, newTrace).getStackTrace().length);

        // first element in the original trace should remain first:
        assertEquals(rawTrace[0], ex1.getStackTrace()[0]);
    }

    @Test
    public void rewriteStacktrace_prepend_addsTraeToStart() {
        RxTracer.setMode(RxTracer.Mode.PREPEND);
        final StackTraceElement[] newTrace = new Exception().getStackTrace();
        final Exception ex1 = new Exception();
        final StackTraceElement[] rawTrace = ex1.getStackTrace();

        assertEquals(rawTrace.length + newTrace.length, RxTracer.rewriteStackTrace(ex1, newTrace).getStackTrace().length);

        // first element in the new trace should now be first:
        assertEquals(newTrace[0], ex1.getStackTrace()[0]);
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