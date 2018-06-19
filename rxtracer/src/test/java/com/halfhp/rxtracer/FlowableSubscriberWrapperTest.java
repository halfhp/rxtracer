package com.halfhp.rxtracer;

import com.halfhp.rxtracer.test.ExampleService;
import com.halfhp.rxtracer.test.TraceChecker;

import io.reactivex.BackpressureStrategy;
import org.junit.Test;

public class FlowableSubscriberWrapperTest extends WrapperTest {


    @Test
    public void flowable_runtimeException_hasSubscribeInStackTrace() throws Exception {
        final TraceChecker traceChecker = new TraceChecker(FlowableSubscriberWrapperTest.class, 17);
        exampleService.getFooObservable()
                .toFlowable(BackpressureStrategy.DROP)
                .subscribe(new ExampleService.FailOnSuccessConsumer(), new ExampleService.CheckTraceOnErrorConsumer(traceChecker));
        Thread.sleep(100);
        traceChecker.assertValid();
    }
}
