package io.reactivex;

import com.halfhp.rxtracer.FlowableSubscriberWrapper;

import com.halfhp.rxtracer.StackTraceRewriter;
import org.reactivestreams.Subscriber;

import io.reactivex.annotations.NonNull;

public class TracingFlowable<T> extends Flowable<T> {

    private final Flowable<T> wrapped;
    private final StackTraceRewriter rewriter;

    public TracingFlowable(@NonNull Flowable<T> wrapped, @NonNull StackTraceRewriter rewriter) {
        this.wrapped = wrapped;
        this.rewriter = rewriter;
    }

    @Override
    protected void subscribeActual(Subscriber<? super T> observer) {
        wrapped.subscribeActual(new FlowableSubscriberWrapper<>(observer, rewriter));
    }

}
