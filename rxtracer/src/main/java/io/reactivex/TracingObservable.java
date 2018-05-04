package io.reactivex;

import com.halfhp.rxtracer.RxTracer;
import com.halfhp.rxtracer.TracingObserver;

import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class TracingObservable<T> extends Observable<T> {

    private final Observable<T> wrapped;

    public TracingObservable(@NonNull Observable<T> wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    protected void subscribeActual(io.reactivex.Observer<? super T> observer) {
        wrapped.subscribeActual(new ObserverWrapper<>(observer));
    }

    private static final class ObserverWrapper<T> extends TracingObserver<Observer<? super T>> implements io.reactivex.Observer<T> {

        ObserverWrapper(@NonNull Observer<? super T> wrapped) {
            super((wrapped));
        }

        @Override
        public void onSubscribe(Disposable d) {
            wrapped.onSubscribe(d);
        }

        @Override
        public void onNext(T t) {
            wrapped.onNext(t);
        }

        @Override
        public void onError(Throwable t) {
            wrapped.onError(RxTracer.rewriteStackTrace(t, this.stackTrace));
        }

        @Override
        public void onComplete() {
            wrapped.onComplete();
        }
    }
}
