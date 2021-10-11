package com.halfhp.rxtracer;

import io.reactivex.annotations.NonNull;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class FlowableSubscriberWrapper<T> extends TracingObserver<Subscriber<? super T>> implements Subscriber<T> {

    public FlowableSubscriberWrapper(@NonNull Subscriber<? super T> wrapped, @NonNull StackTraceRewriter rewriter) {
        super(wrapped, rewriter);
    }

    @Override
    public void onSubscribe(Subscription s) {
        wrapped.onSubscribe(s);
    }

    @Override
    public void onNext(T t) {
        wrapped.onNext(t);
    }

    @Override
    public void onError(Throwable e) {
        wrapped.onError(rewriter.rewrite(e, this.stackTrace));
    }

    @Override
    public void onComplete() {
        wrapped.onComplete();
    }
}
