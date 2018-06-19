package com.halfhp.rxtracer;

import com.halfhp.rxtracer.test.ExampleService;
import com.halfhp.rxtracer.test.TraceChecker;

import org.junit.Test;

public class ObserverWrapperTest extends WrapperTest {

        @Test
    public void observable_runtimeException_hasSubscribeInStackTrace() throws Exception {
        final TraceChecker traceChecker = new TraceChecker(ObserverWrapperTest.class, 14);
        exampleService.getFooObservable()
                .subscribe(new ExampleService.FailOnSuccessConsumer(), new ExampleService.CheckTraceOnErrorConsumer(traceChecker));
        Thread.sleep(100);
        traceChecker.assertValid();
    }
}
