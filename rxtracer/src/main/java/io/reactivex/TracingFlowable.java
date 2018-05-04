package io.reactivex;

import com.halfhp.rxtracer.RxTracer;
import com.halfhp.rxtracer.TracingObserver;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import io.reactivex.annotations.NonNull;

public class TracingFlowable<T> extends Flowable<T> {

    private final Flowable<T> wrapped;

    public TracingFlowable(@NonNull Flowable<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    protected void subscribeActual(Subscriber<? super T> observer) {
        wrapped.subscribeActual(new FlowableSubscriberWrapper<>(observer));
    }

    private static final class FlowableSubscriberWrapper<T> extends TracingObserver<Subscriber<? super T>> implements Subscriber<T> {

        FlowableSubscriberWrapper(@NonNull Subscriber<? super T> wrapped) {
            super((wrapped));
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
            wrapped.onError(RxTracer.rewriteStackTrace(e, this.stackTrace));
        }

        @Override
        public void onComplete() {
            wrapped.onComplete();
        }
    }
}
