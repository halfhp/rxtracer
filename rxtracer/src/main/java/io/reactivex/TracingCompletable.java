package io.reactivex;

import android.support.annotation.NonNull;

import com.halfhp.rxtracer.RxTracer;

import io.reactivex.disposables.Disposable;

public class TracingCompletable extends Completable {

    private final Completable wrapped;

    public TracingCompletable(@NonNull Completable wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    protected void subscribeActual(CompletableObserver co) {
        wrapped.subscribeActual(new Observer(co));
    }

    private static final class Observer extends RxTracer.TracingObserverBase<CompletableObserver> implements CompletableObserver {

        Observer(@NonNull CompletableObserver wrapped) {
            super((wrapped));
        }

        @Override
        public void onSubscribe(Disposable d) {
            wrapped.onSubscribe(d);
        }

        @Override
        public void onComplete() {
            wrapped.onComplete();
        }

        @Override
        public void onError(Throwable t) {
            wrapped.onError(RxTracer.rewriteStackTrace(t, this.stackTrace));
        }
    }
}
