package io.reactivex;

import com.halfhp.rxtracer.RxTracer;
import com.halfhp.rxtracer.TracingObserver;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class TracingSingle<T> extends Single<T> {

    private final Single<T> wrapped;

    public TracingSingle(@NonNull Single<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    protected void subscribeActual(SingleObserver<? super T> observer) {
        wrapped.subscribeActual(new SingleObserverWrapper<>(observer));
    }

    private static final class SingleObserverWrapper<T> extends TracingObserver<SingleObserver<? super T>> implements SingleObserver<T> {

        SingleObserverWrapper(@NonNull SingleObserver<? super T> wrapped) {
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
    }
}
