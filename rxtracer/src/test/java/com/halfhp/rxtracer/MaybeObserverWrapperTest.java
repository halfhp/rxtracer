package com.halfhp.rxtracer;

import com.halfhp.rxtracer.test.ExampleService;
import com.halfhp.rxtracer.test.TraceChecker;

import org.junit.Test;

public class MaybeObserverWrapperTest extends WrapperTest {


    @Test
    public void maybe_runtimeException_hasSubscribeInStackTrace() throws Exception {
        final TraceChecker traceChecker = new TraceChecker(MaybeObserverWrapperTest.class, 16);
        exampleService.getFooObservable()
                .singleElement()
                .subscribe(new ExampleService.FailOnSuccessConsumer(), new ExampleService.CheckTraceOnErrorConsumer(traceChecker));
        Thread.sleep(100);
        traceChecker.assertValid();
    }
}
