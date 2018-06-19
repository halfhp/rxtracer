package com.halfhp.rxtracer;

import com.halfhp.rxtracer.test.ExampleService;
import com.halfhp.rxtracer.test.TraceChecker;

import org.junit.Test;

public class SingleObserverWrapperTest extends WrapperTest {

    @Test
    public void single_runtimeException_hasSubscribeInStackTrace() throws Exception {
        final TraceChecker traceChecker = new TraceChecker(SingleObserverWrapperTest.class, 15);
        exampleService.getFooObservable()
                .singleOrError()
                .subscribe(new ExampleService.FailOnSuccessConsumer(), new ExampleService.CheckTraceOnErrorConsumer(traceChecker));
        Thread.sleep(100);
        traceChecker.assertValid();
    }
}
