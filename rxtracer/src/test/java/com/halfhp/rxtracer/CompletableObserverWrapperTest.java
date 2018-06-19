package com.halfhp.rxtracer;

import com.halfhp.rxtracer.test.ExampleService;
import com.halfhp.rxtracer.test.TraceChecker;

import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.functions.Function;
import org.junit.Test;

public class CompletableObserverWrapperTest extends WrapperTest {

    @Test
    public void completable_runtimeException_hasSubscribeInStackTrace() throws Exception {
        final TraceChecker traceChecker = new TraceChecker(CompletableObserverWrapperTest.class, 23);
        exampleService.getFooObservable()
                .flatMapCompletable(new Function<ExampleService.Foo, CompletableSource>() {
                    @Override
                    public CompletableSource apply(ExampleService.Foo foo) {
                        return Completable.complete();
                    }
                })
                .subscribe(new ExampleService.FailOnSuccessAction(), new ExampleService.CheckTraceOnErrorConsumer(traceChecker));
        Thread.sleep(100);
        traceChecker.assertValid();
    }
}
