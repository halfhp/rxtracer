package io.reactivex;

import com.halfhp.rxtracer.RxTracer;
import com.halfhp.rxtracer.TracingObserver;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class TracingMaybe<T> extends Maybe<T> {

    private final Maybe<T> wrapped;

    public TracingMaybe(@NonNull Maybe<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    protected void subscribeActual(MaybeObserver<? super T> observer) {
        wrapped.subscribeActual(new MaybeObserverWrapper<>(observer));
    }

    private static final class MaybeObserverWrapper<T> extends TracingObserver<MaybeObserver<? super T>> implements MaybeObserver<T> {

        MaybeObserverWrapper(@NonNull MaybeObserver<? super T> wrapped) {
            super((wrapped));
        }

        @Override
        public void onSubscribe(Disposable d) {
            wrapped.onSubscribe(d);
        }

        @Override
        public void onSuccess(T t) {
            wrapped.onSuccess(t);
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
